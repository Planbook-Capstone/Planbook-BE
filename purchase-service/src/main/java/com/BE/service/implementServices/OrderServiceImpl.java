package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.enums.TransactionType;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.feign.EmailServiceClient;
import com.BE.feign.IdentityServiceClient;
import com.BE.mapper.OrderMapper;
import com.BE.model.entity.Order;
import com.BE.model.entity.OrderHistory;
import com.BE.model.entity.PaymentTransaction;
import com.BE.model.entity.SubscriptionPackage;
import com.BE.model.request.*;
import com.BE.model.response.*;
import com.BE.repository.OrderHistoryRepository;
import com.BE.repository.OrderRepository;
import com.BE.repository.PaymentTransactionRepository;
import com.BE.repository.SubscriptionPackageRepository;
import com.BE.service.interfaceServices.IOrderService;
import com.BE.service.interfaceServices.IPaymentService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final AccountUtils accountUtils;
    private final OrderMapper orderMapper;
    private final IPaymentService paymentService;
    private final TaskScheduler taskScheduler;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PageUtil pageUtil;
    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final IdentityServiceClient identityServiceClient;
    private final EmailServiceClient emailServiceClient;
    private final DateNowUtils dateNowUtils;

    @Value("${spring.sendgrid.template.order}")
    private String templateOrder;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {

        UUID currentUserId = accountUtils.getCurrentUserId();

        SubscriptionPackage subscriptionPackage = subscriptionPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new NotFoundException("Gói dịch vụ này không tồn tại"));

        List<StatusEnum> statuses = List.of(StatusEnum.PENDING, StatusEnum.RETRY);
        boolean existsPending = orderRepository.existsByUserIdAndSubscriptionPackageIdAndStatusIn(
                currentUserId,
                request.getPackageId(),
                statuses
        );

        if (existsPending) {
            throw new IllegalStateException("Bạn đã có đơn hàng đang chờ xử lý cho gói dịch vụ này. Vui lòng hoàn tất hoặc huỷ trước khi tạo mới.");
        }
        // Tạo Order mới
        Order order = Order.builder()
                .userId(currentUserId)
//                .userId(UUID.randomUUID())
                .amount(subscriptionPackage.getPrice())
                .status(StatusEnum.PENDING)
                .build();
        order.addSubcriptionPackage(subscriptionPackage);




        saveHistory(order, null, StatusEnum.PENDING, "Tạo đơn hàng mới");
        Order savedOrder = orderRepository.save(order);

        // Tự động tạo Payment sau khi Order được tạo
        CreatePaymentRequestDTO paymentRequest = new CreatePaymentRequestDTO();
        paymentRequest.setOrder(savedOrder);
        paymentRequest.setAmount(savedOrder.getAmount());
        paymentRequest.setDescription("Thanh toán PlanBookAI");

        PaymentTransaction paymentLink = paymentService.createPaymentLink(paymentRequest);
        scheduleAutoExpire(paymentLink);


        OrderResponseDTO responseDTO = orderMapper.toOrderResponseDTO(savedOrder);
        responseDTO.setCheckoutUrl(paymentLink.getCheckoutUrl());
        responseDTO.setQrCode(paymentLink.getQrCode());
        return responseDTO;
    }


    private void scheduleAutoExpire(PaymentTransaction txn) {
        Date targetTime = Date.from(txn.getExpiredAt()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toInstant());


        taskScheduler.schedule(() -> {
            // runnable logic
            PaymentTransaction latestTxn = paymentTransactionRepository.findById(txn.getId())
                    .orElse(null);
            Order order = latestTxn.getOrder();
            if (latestTxn != null && (StatusEnum.PENDING.equals(order.getStatus()) || StatusEnum.RETRY.equals(order.getStatus()))
                    && (StatusEnum.PENDING.equals(latestTxn.getStatus()) || StatusEnum.RETRY.equals(latestTxn.getStatus()))) {
                latestTxn.setStatus(StatusEnum.EXPIRED);
                paymentTransactionRepository.save(latestTxn);
                order.setStatus(StatusEnum.EXPIRED);
                saveHistory(order, order.getStatus(), StatusEnum.EXPIRED, "Tự động hết hạn do quá hạn thanh toán");
                orderRepository.save(order);
                System.out.println("✅ Auto-expired payment " + txn.getId() + " và order " + order.getId());
            }
        }, targetTime);
    }

    @Override
    public OrderResponseDTO getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));

        return orderMapper.toOrderResponseDTO(order);
    }

    @Override
    public List<OrderHistoryResponseDTO> getOrderHistory(UUID orderId) {
        List<OrderHistory> histories = orderHistoryRepository.findByOrderId(orderId);

        return histories.stream()
                .map(orderMapper::toOrderHistoryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(UUID orderId, StatusEnum newStatus, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
        StatusEnum fromStatus = order.getStatus();

        order.setStatus(newStatus);
        saveHistory(order, fromStatus, newStatus, note);
        orderRepository.save(order);

        OrderResponseDTO responseDTO = orderMapper.toOrderResponseDTO(order);

        // Nếu update sang CANCELLED thì huỷ các Payment PENDING
        if (StatusEnum.CANCELLED.equals(newStatus)) {
            CancelPaymentRequestDTO cancelRequest = new CancelPaymentRequestDTO();
            cancelRequest.setOrderId(orderId);
            cancelRequest.setCancellationReason(note != null ? note : "Order đã bị huỷ.");
            paymentService.cancelAllPendingTransactions(cancelRequest);
        } else if (StatusEnum.RETRY.equals(newStatus)) {
            // Nếu quay lại PENDING ⇒ retry payment
            RetryPaymentRequestDTO retryRequest = new RetryPaymentRequestDTO();
            retryRequest.setOrder(order);
            PaymentTransaction paymentTransaction = paymentService.retryPayment(retryRequest);
            responseDTO.setCheckoutUrl(paymentTransaction.getCheckoutUrl());
            responseDTO.setQrCode(paymentTransaction.getQrCode());
            scheduleAutoExpire(paymentTransaction);
        }
        return responseDTO;
    }

    private void saveHistory(Order order, StatusEnum from, StatusEnum to, String note) {
        OrderHistory history = OrderHistory.builder()
                .order(order)
                .fromStatus(from)
                .toStatus(to)
                .note(note)
                .build();
        order.addHistory(history);
    }


    @Override
    public void handlePaymentResult(ObjectNode body) throws Exception {

        PaymentTransaction txn = paymentService.handlePayosWebhook(body);
        if (txn != null) {
            Order order = txn.getOrder();
            StatusEnum fromStatus = order.getStatus();
            StatusEnum newStatus = txn.getStatus();
            // Nếu trạng thái khác thì update + save history
            if (!fromStatus.equals(newStatus)) {
                order.setStatus(newStatus);
                saveHistory(order, fromStatus, newStatus, getNoteForStatus(newStatus));
                order = orderRepository.save(order);
            }
            if (StatusEnum.PAID.equals(newStatus)) {
                try {
                    WalletTransactionRequest walletTransactionRequest = WalletTransactionRequest.builder()
                            .orderId(order.getId())
                            .description("Nạp token từ gói " + order.getSubscriptionPackage().getName())
                            .tokenChange(order.getSubscriptionPackage().getTokenAmount())
                            .type(TransactionType.RECHARGE)
                            .userId(order.getUserId())
                            .build();

                    DataResponseDTO<WalletTransactionResponse> wallet = identityServiceClient.recharge(walletTransactionRequest);

                    DataResponseDTO<UserResponse> user = identityServiceClient.getUserById(order.getUserId());
                    System.out.println(user.getData().getFullName());
                    EmailDataRequest emailDataRequest =  new EmailDataRequest();
                    emailDataRequest.setToEmail(user.getData().getEmail());
                    emailDataRequest.setTemplateId(templateOrder);

                    Map<String, String> dynamicData = new HashMap<>();
                    dynamicData.put("package_name", order.getSubscriptionPackage().getName());
                    dynamicData.put("fullname", user.getData().getFullName());

                    emailDataRequest.setDynamicData(dynamicData);
                    String response = emailServiceClient.sendTemplateEmail(emailDataRequest);
                }catch (FeignException e) {
                    throw e; // Ném lại FeignException
                } catch (NullPointerException e) {
                    throw e; // Ném lại NullPointerException
                } catch (Exception e) {
                    throw e; // Ném lại Exception chung
                }
            }
        }
    }

    private String getNoteForStatus(StatusEnum status) {
        switch (status) {
            case PAID:
                return "Đơn hàng đã thanh toán thành công qua PayOS";
            case CANCELLED:
                return "Giao dịch đã bị huỷ qua PayOS";
            case EXPIRED:
                return "Giao dịch đã hết hạn qua PayOS";
            case FAILED:
                return "Giao dịch thất bại qua PayOS";
            default:
                return "Cập nhật trạng thái tự động qua PayOS: " + status;
        }
    }


    @Override
    public Page<OrderResponseDTO> getOrdersWithFilter(StatusEnum status, UUID userId, UUID packageId,
                                                      int offset, int pageSize,
                                                      String sortBy, String sortDirection) {
        // Validate offset (bắt đầu từ 1)
        pageUtil.checkOffset(offset);

        // Tạo sort
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        // Tạo pageable (chuyển offset về 0-based index)
        Pageable pageable = PageRequest.of(offset - 1, pageSize, sort);

        // Xây dựng Specification động
        Specification<Order> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (packageId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("subscriptionPackage").get("id"), packageId));
        }

        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), userId));
        }

        // Trả về kết quả đã map DTO
        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toOrderResponseDTO);
    }

    @Override
    public OrderResponseDTO cancelPaymentTransactions(Long orderCode) {
        Order order =  paymentService.cancelPaymentPayos(orderCode);
        StatusEnum fromStatus = order.getStatus();
        order.setStatus(StatusEnum.CANCELLED);
        saveHistory(order, fromStatus, StatusEnum.CANCELLED, "Huỷ thanh toán từ Payos");
        return orderMapper.toOrderResponseDTO(orderRepository.save(order));
    }


}

