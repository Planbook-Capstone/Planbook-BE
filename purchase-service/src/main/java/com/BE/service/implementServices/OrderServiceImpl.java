package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.OrderMapper;
import com.BE.model.entity.Order;
import com.BE.model.entity.OrderHistory;
import com.BE.model.entity.PaymentTransaction;
import com.BE.model.entity.SubscriptionPackage;
import com.BE.model.request.CancelPaymentRequestDTO;
import com.BE.model.request.CreateOrderRequestDTO;
import com.BE.model.request.CreatePaymentRequestDTO;
import com.BE.model.request.RetryPaymentRequestDTO;
import com.BE.model.response.OrderHistoryResponseDTO;
import com.BE.model.response.OrderResponseDTO;
import com.BE.repository.OrderHistoryRepository;
import com.BE.repository.OrderRepository;
import com.BE.repository.PaymentTransactionRepository;
import com.BE.repository.SubscriptionPackageRepository;
import com.BE.service.interfaceServices.IOrderService;
import com.BE.service.interfaceServices.IPaymentService;
import com.BE.service.interfaceServices.ISubscriptionPackageService;
import com.BE.utils.AccountUtils;
import com.BE.utils.PageUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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


    @Override
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        SubscriptionPackage subscriptionPackage = subscriptionPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new NotFoundException("Gói dịch vụ này không tồn tại"));

        // Tạo Order mới
        Order order = Order.builder()
                .userId(accountUtils.getCurrentUserId())
//                .userId(UUID.randomUUID())
                .amount(subscriptionPackage.getPrice())
                .status(StatusEnum.PENDING)
                .build();
        order.addSubcriptionPackage(subscriptionPackage);


        Order savedOrder = orderRepository.save(order);

        saveHistory(savedOrder, null, StatusEnum.PENDING, "Tạo đơn hàng mới");

        // Tự động tạo Payment sau khi Order được tạo
        CreatePaymentRequestDTO paymentRequest = new CreatePaymentRequestDTO();
        paymentRequest.setOrder(savedOrder);
        paymentRequest.setAmount(savedOrder.getAmount());
        paymentRequest.setDescription("Thanh toán PlanBookAI");

        PaymentTransaction paymentLink = paymentService.createPaymentLink(paymentRequest);
        scheduleAutoExpire(paymentLink);


        OrderResponseDTO responseDTO = orderMapper.toOrderResponseDTO(savedOrder);
        responseDTO.setCheckoutUrl(paymentLink.getCheckoutUrl());

        return responseDTO;
    }


    private void scheduleAutoExpire(PaymentTransaction txn) {
        LocalDateTime now = LocalDateTime.now();
        long delayMillis = java.time.Duration.between(now, txn.getExpiredAt()).toMillis();

        taskScheduler.schedule(() -> {
            // runnable logic
            PaymentTransaction latestTxn = paymentTransactionRepository.findById(txn.getId())
                    .orElse(null);
            Order order = latestTxn.getOrder();
            if (latestTxn != null && StatusEnum.PENDING.equals(latestTxn.getStatus()) && StatusEnum.PENDING.equals(order.getStatus())) {
                latestTxn.setStatus(StatusEnum.EXPIRED);
                paymentTransactionRepository.save(latestTxn);
                order.setStatus(StatusEnum.EXPIRED);
                saveHistory(order, order.getStatus(), StatusEnum.EXPIRED, "Tự động hết hạn do quá hạn thanh toán");
                orderRepository.save(order);
                System.out.println("✅ Auto-expired payment " + txn.getId() + " và order " + order.getId());
            }
        }, new Date(System.currentTimeMillis() + delayMillis));
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
        orderRepository.save(order);

        saveHistory(order, fromStatus, newStatus, note);

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
        orderHistoryRepository.save(history);
    }


    @Override
    @Transactional
    public void handlePaymentResult(ObjectNode body) throws Exception {

        PaymentTransaction txn = paymentService.handlePayosWebhook(body);
        if (txn != null) {
            System.out.println(txn);
            Order order = txn.getOrder();
            StatusEnum fromStatus = order.getStatus();
            StatusEnum newStatus = txn.getStatus();

            // Nếu trạng thái khác thì update + save history
            if (!fromStatus.equals(newStatus)) {
                order.setStatus(newStatus);
                orderRepository.save(order);

                saveHistory(order, fromStatus, newStatus, getNoteForStatus(newStatus));
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
    public Page<OrderResponseDTO> getOrdersWithFilter(StatusEnum status, UUID userId,
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

        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), userId));
        }

        // Trả về kết quả đã map DTO
        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toOrderResponseDTO);
    }


}

