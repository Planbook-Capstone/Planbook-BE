package com.BE.service.implementServices;

import com.BE.enums.GatewayEnum;
import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.BusinessException;
import com.BE.exception.exceptions.InvalidSignatureException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.PaymentTransactionMapper;
import com.BE.model.entity.Order;
import com.BE.model.entity.PaymentTransaction;
import com.BE.model.request.CancelPaymentRequestDTO;
import com.BE.model.request.CreatePaymentRequestDTO;
import com.BE.model.request.RetryPaymentRequestDTO;
import com.BE.model.response.CancelPaymentResponseDTO;
import com.BE.model.response.PaymentTransactionResponse;
import com.BE.repository.PaymentTransactionRepository;
import com.BE.service.interfaceServices.IPaymentService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.*;


import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PayOS payos;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentMapper;
    private final ObjectMapper objectMapper;
    private final AccountUtils accountUtils;
    private final DateNowUtils dateNowUtils;


    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Override
    @Transactional
    public PaymentTransaction createPaymentLink(CreatePaymentRequestDTO request) {
        try {
            long orderCode = System.currentTimeMillis();
            ItemData item = ItemData.builder()
                    .name("Thanh toán đơn hàng")
                    .quantity(1)
                    .price(request.getAmount().intValue())
                    .build();

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(request.getAmount().intValue())
                    .description(request.getDescription())
                    .items(Collections.singletonList(item))
                    .cancelUrl(cancelUrl)
                    .returnUrl(returnUrl)
                    .build();

            CheckoutResponseData response = payos.createPaymentLink(paymentData);

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .order(request.getOrder())
                    .amount(request.getAmount())
                    .status(StatusEnum.PENDING)
                    .description(request.getDescription())
                    .expiredAt(dateNowUtils.getCurrentDateTimeHCM().plusMinutes(15))
                    .gateway(GatewayEnum.PAYOS)
                    .payosOrderCode(orderCode)
                    .payosTransactionId(response.getPaymentLinkId())
                    .checkoutUrl(response.getCheckoutUrl())
                    .qrCode(response.getQrCode())
                    .createdAt(dateNowUtils.getCurrentDateTimeHCM())
                    .updatedAt(dateNowUtils.getCurrentDateTimeHCM())
                    .build();
            request.getOrder().addTransaction(transaction);

            transaction = paymentTransactionRepository.save(transaction);

            return transaction;
        } catch (Exception e) {
            System.err.println("Lỗi tạo link thanh toán PayOS: " + e.getMessage());
            throw new RuntimeException("Không thể tạo link thanh toán PayOS", e);
        }
    }


    @Override
    @Transactional
    public PaymentTransaction retryPayment(RetryPaymentRequestDTO request) {

        PaymentTransaction root = paymentTransactionRepository.findRootTransactionByOrderId(request.getOrder().getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy transaction gốc của orderId: " + request.getOrder().getId()));

        // 2. Lấy transaction cuối cùng trong chain
        PaymentTransaction latest = getLatestInRetryChain(root);

        // 3. Kiểm tra trạng thái
        if (StatusEnum.PAID.equals(latest.getStatus()) || StatusEnum.PENDING.equals(latest.getStatus()) || StatusEnum.RETRY.equals(latest.getStatus())) {
            throw new BusinessException("Giao dịch trước đó vẫn đang hoạt động, vui lòng không tạo lại.");
        }

        // 4. Gọi tạo mới
        CreatePaymentRequestDTO newReq = new CreatePaymentRequestDTO();
        newReq.setOrder(request.getOrder());
        newReq.setAmount(request.getOrder().getAmount());
        newReq.setDescription("Thanh toán PlanBookAI");

        PaymentTransaction response = createPaymentLink(newReq);

        response.setStatus(StatusEnum.RETRY);
        response.setParentTransactionId(latest.getId());
        response = paymentTransactionRepository.save(response);

        return response;
    }

    @Override
    @Transactional
    public PaymentTransaction handlePayosWebhook(ObjectNode body) throws Exception {
            Webhook webhookPayload = objectMapper.treeToValue(body, Webhook.class);
            WebhookData webhookData;
            try {
                webhookData = payos.verifyPaymentWebhookData(webhookPayload);
            } catch (Exception e) {
                System.err.println("❌ Lỗi xác thực chữ ký webhook: " + e.getMessage());
                throw new InvalidSignatureException("Chữ ký webhook không hợp lệ.");
            }
            String payosTransactionId = webhookData.getPaymentLinkId();
            PaymentTransaction txn = paymentTransactionRepository
                    .findByPayosTransactionId(payosTransactionId)
                    .orElseThrow(() -> new InvalidSignatureException("Không tìm thấy giao dịch với PayOS ID: " + payosTransactionId));

            PaymentLinkData paymentLinkData = payos.getPaymentLinkInformation(webhookData.getOrderCode());
            String actualPayosStatus = paymentLinkData.getStatus().toUpperCase(Locale.ROOT);
            System.out.println(actualPayosStatus);
            String payosStatusDescription = webhookData.getDescription();

            if (!StatusEnum.PENDING.equals(txn.getStatus()) && !StatusEnum.RETRY.equals(txn.getStatus())) {
                return txn;
            }
            Map<String, Object> rawPayload = objectMapper.convertValue(body, new TypeReference<>() {});
            txn.setWebhookPayload(rawPayload);

            StatusEnum newStatus;
            String failureReason = null;

            switch (actualPayosStatus) {
                case "PAID":
                    if (paymentTransactionRepository.existsByOrderAndGatewayAndStatus(txn.getOrder(), GatewayEnum.PAYOS, StatusEnum.PAID)) {
                        return txn;
                    }
                    newStatus = StatusEnum.PAID;
                    break;
                case "PENDING":
                    return txn;
                case "CANCELLED":
                    newStatus = StatusEnum.CANCELLED;
                    failureReason = (payosStatusDescription != null && !payosStatusDescription.isEmpty())
                            ? payosStatusDescription : "Giao dịch đã bị huỷ.";
                    if (failureReason.toLowerCase(Locale.ROOT).contains("hết hạn") ||
                            failureReason.toLowerCase(Locale.ROOT).contains("expired")) {
                        newStatus = StatusEnum.EXPIRED;
                        failureReason = "Giao dịch đã hết hạn.";
                    }
                    break;
                default:
                    newStatus = StatusEnum.FAILED;
                    failureReason = "Trạng thái không xác định từ PayOS: " + actualPayosStatus +
                            ". Mô tả: " + (payosStatusDescription != null ? payosStatusDescription : "N/A");
                    break;
            }

            txn.setStatus(newStatus);
            txn.setFailureReason(failureReason);
            paymentTransactionRepository.save(txn);

            return txn;
    }


    @Override
    public List<PaymentTransactionResponse> getTransactionsByOrderId(UUID orderId) {
        List<PaymentTransactionResponse> chain = new ArrayList<>();

        paymentTransactionRepository.findRootTransactionByOrderId(orderId).ifPresent(root -> {
            buildRetryChain(root, chain);
        });

        return chain;
    }

    private void buildRetryChain(PaymentTransaction current, List<PaymentTransactionResponse> chain) {
        chain.add(paymentMapper.toResponse(current));

        Optional<PaymentTransaction> child = paymentTransactionRepository.findByParentTransactionId(current.getId());
        child.ifPresent(c -> buildRetryChain(c, chain));
    }

    private PaymentTransaction getLatestInRetryChain(PaymentTransaction current) {
        Optional<PaymentTransaction> child = paymentTransactionRepository.findByParentTransactionId(current.getId());
        return child.map(this::getLatestInRetryChain).orElse(current);
    }

    @Override
    public CancelPaymentResponseDTO cancelAllPendingTransactions(CancelPaymentRequestDTO request) {
        UUID orderId = request.getOrderId();

        List<PaymentTransaction> transactions = paymentTransactionRepository.findAllByOrderId(orderId);

        if (transactions.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bất kỳ giao dịch nào với orderId: " + orderId);
        }

        int cancelledCount = 0;
        for (PaymentTransaction txn : transactions) {
            if (txn.getStatus().equals(StatusEnum.PENDING) || txn.getStatus().equals(StatusEnum.RETRY)) {
                try {
                    if (txn.getPayosOrderCode() != null) {
                        payos.cancelPaymentLink(
                                txn.getPayosOrderCode(),
                                request.getCancellationReason() != null ? request.getCancellationReason() : "Huỷ đơn hàng"
                        );
                    }
                } catch (Exception e) {
                    // log lỗi nhưng tiếp tục huỷ các transaction khác
                    e.printStackTrace();
                }

                txn.setStatus(StatusEnum.CANCELLED);
                paymentTransactionRepository.save(txn);
                cancelledCount++;
            }
        }

        return CancelPaymentResponseDTO.builder()
                .cancelledCount(cancelledCount)
                .message(cancelledCount > 0
                        ? "Đã huỷ toàn bộ các giao dịch PENDING"
                        : "Không có giao dịch PENDING để huỷ")
                .build();
    }

    @Override
    public Order cancelPaymentPayos(Long orderCode) {

        PaymentTransaction txn = paymentTransactionRepository.findByPayosOrderCode(orderCode).orElseThrow(() -> new NotFoundException("Không tìm thấy giao dịch này"));

        Order order = txn.getOrder();

        // Chỉ cho phép huỷ nếu transaction và order đều đang chờ
        boolean txnPending = txn.getStatus().equals(StatusEnum.PENDING) || txn.getStatus().equals(StatusEnum.RETRY);
        boolean orderPending = order.getStatus().equals(StatusEnum.PENDING) || order.getStatus().equals(StatusEnum.RETRY);

        if (!(txnPending && orderPending)) {
            throw new IllegalStateException("Không thể huỷ: giao dịch hoặc đơn hàng không còn ở trạng thái chờ thanh toán.");
        }

        try {
            if (txn.getPayosOrderCode() != null) {
                payos.cancelPaymentLink(
                        txn.getPayosOrderCode(),
                        "Huỷ thanh toán từ Payos"
                );
            }
        } catch (Exception e) {
            // log lỗi nhưng vẫn xử lý huỷ local
            e.printStackTrace();
        }

            txn.setStatus(StatusEnum.CANCELLED);
            paymentTransactionRepository.save(txn);

        return order;
    }

}
