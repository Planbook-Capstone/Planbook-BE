package com.BE.service.implementServices;

import com.BE.enums.GatewayEnum;
import com.BE.enums.PaymentStatusEnum;
import com.BE.exception.exceptions.InvalidSignatureException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.PaymentTransactionMapper;
import com.BE.model.entity.PaymentTransaction;
import com.BE.model.request.CancelPaymentRequestDTO;
import com.BE.model.request.CreatePaymentRequestDTO;
import com.BE.model.request.RetryPaymentRequestDTO;
import com.BE.model.response.CancelPaymentResponseDTO;
import com.BE.model.response.PaymentLinkResponseDTO;
import com.BE.model.response.PaymentTransactionResponse;
import com.BE.repository.PaymentTransactionRepository;
import com.BE.service.interfaceServices.IPaymentService;
import com.BE.utils.AccountUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.*;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PayOS payos;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentMapper;
    private final ObjectMapper objectMapper;
    private final AccountUtils accountUtils;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Override
    @Transactional
    public PaymentLinkResponseDTO createPaymentLink(CreatePaymentRequestDTO request) {
        // ✅ Kiểm tra trước khi tạo mới
        boolean existsPending = paymentTransactionRepository.existsByOrderIdAndGatewayAndStatus(
                request.getOrderId(), GatewayEnum.PAYOS, PaymentStatusEnum.PENDING
        );

        if (existsPending) {
            throw new IllegalStateException("Đã tồn tại một giao dịch thanh toán PAYOS đang chờ xử lý cho đơn hàng này.");
        }

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
                    .userId(accountUtils.getCurrentUserId())
//                    .userId(UUID.randomUUID())
                    .orderId(request.getOrderId())
                    .amount(request.getAmount())
                    .status(PaymentStatusEnum.PENDING)
                    .description(request.getDescription())
                    .expiredAt(LocalDateTime.now().plusMinutes(15))
                    .gateway(GatewayEnum.PAYOS)
                    .payosOrderCode(orderCode)
                    .payosTransactionId(response.getPaymentLinkId())
                    .checkoutUrl(response.getCheckoutUrl())
                    .build();

            paymentTransactionRepository.save(transaction);

            PaymentLinkResponseDTO dto = paymentMapper.toPaymentLinkResponseDTO(transaction);

            dto.setOrderCode(orderCode);
            return dto;
        } catch (Exception e) {
            System.err.println("Lỗi tạo link thanh toán PayOS: " + e.getMessage());
            throw new RuntimeException("Không thể tạo link thanh toán PayOS", e);
        }
    }

    @Override
    @Transactional
    public PaymentLinkResponseDTO retryPayment(RetryPaymentRequestDTO request) {
        UUID orderId = request.getOrderId();

        // 1. Lấy root transaction của order
        PaymentTransaction root = paymentTransactionRepository.findRootTransactionByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy transaction gốc của orderId: " + orderId));

        // 2. Lấy transaction cuối cùng trong chain
        PaymentTransaction latest = getLatestInRetryChain(root);

        // 3. Kiểm tra trạng thái
        if (latest.getStatus() == PaymentStatusEnum.PAID || latest.getStatus() == PaymentStatusEnum.PENDING) {
            throw new RuntimeException("Không thể retry khi giao dịch cuối cùng đang PENDING hoặc đã PAID.");
        }

        // 4. Gọi tạo mới
        CreatePaymentRequestDTO newReq = new CreatePaymentRequestDTO();
        newReq.setOrderId(orderId);
        newReq.setAmount(latest.getAmount());
        newReq.setDescription("Retry thanh toán cho đơn hàng " + orderId);

        PaymentLinkResponseDTO response = createPaymentLink(newReq);

        // 5. Gán quan hệ retry
        PaymentTransaction newTxn = paymentTransactionRepository.findById(response.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy transaction vừa tạo."));
        newTxn.setParentTransactionId(latest.getId());
        paymentTransactionRepository.save(newTxn);

        return response;
    }

    @Override
    @Transactional
    public ObjectNode handlePayosWebhook(ObjectNode body) throws JsonProcessingException {
        ObjectNode response = objectMapper.createObjectNode();

        try {
            // Bước 1: Chuyển đổi JSON payload sang Webhook object
            Webhook webhookPayload = objectMapper.treeToValue(body, Webhook.class);

            // Bước 2: Xác thực webhook thông qua SDK PayOS
            WebhookData webhookData;
            try {
                webhookData = payos.verifyPaymentWebhookData(webhookPayload);
            } catch (Exception e) {
                System.err.println("❌ Lỗi xác thực chữ ký webhook: " + e.getMessage());
                throw new InvalidSignatureException("Chữ ký webhook không hợp lệ.");
            }

            // Bước 3: Trích xuất orderCode (paymentLinkId) từ webhook đã xác thực
            String payosTransactionId = webhookData.getPaymentLinkId(); // Đây chính là orderCode

            // Bước 4: Truy vấn giao dịch tương ứng trong hệ thống của bạn
            PaymentTransaction txn = paymentTransactionRepository
                    .findByPayosTransactionId(payosTransactionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với PayOS ID: " + payosTransactionId));

            // Bước 5: Lấy trạng thái ĐỊNH NGHĨA của Payment Link từ PayOS API
            // Đây là bước QUAN TRỌNG NHẤT để có được trạng thái đáng tin cậy
            PaymentLinkData paymentLinkData = payos.getPaymentLinkInformation(webhookData.getOrderCode());
            String actualPayosStatus = paymentLinkData.getStatus().toUpperCase(Locale.ROOT);
            String payosStatusDescription = webhookData.getDescription(); // Lý do/mô tả từ PayOS API

            // Bước 6: Chỉ xử lý giao dịch đang ở trạng thái PENDING hoặc RETRY_ATTEMPTED trong hệ thống của bạn
            // Điều này giúp tránh việc ghi đè trạng thái đã hoàn tất (PAID, CANCELLED, EXPIRED, FAILED)
            if (txn.getStatus() != PaymentStatusEnum.PENDING && txn.getStatus() != PaymentStatusEnum.RETRY_ATTEMPTED) {
                System.out.println("⚠️ Giao dịch " + txn.getId() + " không còn ở trạng thái PENDING/RETRY. Bỏ qua cập nhật trạng thái từ webhook.");
                response.put("error", 0);
                response.put("message", "Webhook delivered - Transaction status already finalized, skipped.");
                response.set("data", null);
                return response;
            }

            // Bước 7: Lưu toàn bộ payload webhook để phục vụ tra cứu/log
            Map<String, Object> rawPayload = objectMapper.convertValue(body, new TypeReference<>() {});
            txn.setWebhookPayload(rawPayload);

            // Bước 8: Cập nhật trạng thái giao dịch theo mã trạng thái từ PayOS API
            PaymentStatusEnum newStatus;
            String failureReason = null;

            switch (actualPayosStatus) {
                case "PAID":
                    // Kiểm tra xem đơn hàng đã được thanh toán trước đó chưa để tránh trùng lặp
                    boolean alreadyPaid = paymentTransactionRepository.existsByOrderIdAndStatus(
                            txn.getOrderId(), PaymentStatusEnum.PAID);
                    if (alreadyPaid) {
                        System.out.println("⚠️ Đơn hàng " + txn.getOrderId() + " đã được thanh toán trước đó. Bỏ qua.");
                        response.put("error", 0);
                        response.put("message", "Webhook delivered - Order already paid, skipped.");
                        response.set("data", null);
                        return response;
                    }
                    newStatus = PaymentStatusEnum.PAID;
                    break;
                case "PENDING":
                    // Nếu webhook đến nhưng API PayOS vẫn báo PENDING, giữ nguyên trạng thái PENDING
                    // hoặc nếu đã PENDING/RETRY_ATTEMPTED thì không làm gì thêm ngoài ghi log
                    newStatus = PaymentStatusEnum.PENDING; // Giữ nguyên trạng thái PENDING
                    System.out.println("ℹ️ Giao dịch " + txn.getId() + " vẫn ở trạng thái PENDING theo PayOS. Không thay đổi trạng thái nội bộ.");
                    // Đối với PENDING, thường chỉ ghi nhận webhook đã nhận thành công
                    response.put("error", 0);
                    response.put("message", "Webhook delivered - Transaction still PENDING according to PayOS.");
                    response.set("data", null);
                    return response; // Trả về sớm nếu không cần cập nhật thêm gì
                case "CANCELLED":
                    // PayOS thường trả về "CANCELLED" cho cả trường hợp bị hủy và hết hạn.
                    // Cần kiểm tra 'reason' để phân biệt nếu muốn ánh xạ riêng sang EXPIRED của bạn.
                    newStatus = PaymentStatusEnum.CANCELLED;
                    failureReason = payosStatusDescription != null && !payosStatusDescription.isEmpty() ? payosStatusDescription : "Giao dịch đã bị hủy.";

                    // Logic để ánh xạ "hết hạn" của PayOS sang EXPIRED của bạn
                    if (failureReason.toLowerCase(Locale.ROOT).contains("hết hạn") ||
                            failureReason.toLowerCase(Locale.ROOT).contains("expired")) {
                        newStatus = PaymentStatusEnum.EXPIRED;
                        failureReason = "Giao dịch đã hết hạn."; // Hoặc giữ nguyên mô tả từ PayOS
                    }
                    break;
                default:
                    // Mọi trạng thái khác không xác định sẽ coi là FAILED
                    newStatus = PaymentStatusEnum.FAILED;
                    failureReason = "Trạng thái không xác định từ PayOS: " + actualPayosStatus + ". Mô tả: " + (payosStatusDescription != null ? payosStatusDescription : "N/A");
                    break;
            }

            // Cập nhật trạng thái và lý do thất bại (nếu có)
            txn.setStatus(newStatus);
            txn.setFailureReason(failureReason);

            // Bước 9: Lưu giao dịch đã cập nhật
            paymentTransactionRepository.save(txn);
            System.out.println("✅ Giao dịch " + txn.getId() + " cập nhật thành công: " + txn.getStatus());

            // Init Response
            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
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
            if (txn.getStatus().equals(PaymentStatusEnum.PENDING)) {
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

                txn.setStatus(PaymentStatusEnum.CANCELLED);
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
}
