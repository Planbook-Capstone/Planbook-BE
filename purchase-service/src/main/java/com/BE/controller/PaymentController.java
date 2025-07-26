package com.BE.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.exception.PayOSException;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Tag(name = "Payment API", description = "API để quản lý thanh toán qua PayOS")
public class PaymentController {

//    private final IPaymentService paymentService;
//    private final ResponseHandler<PaymentLinkResponseDTO> responseHandler;

    private final PayOS payos;

    @Value("${payos.webhook-url}")
    private String webhookUrl;


    @Hidden
    @PostMapping("/confirm-webhook")
    public ResponseEntity<String> confirmWebhook() {
        String confirmedUrl = "";
        try {
            confirmedUrl = payos.confirmWebhook(webhookUrl);
            System.out.println("✅ Webhook đã được xác nhận: " + confirmedUrl);
        } catch (PayOSException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi xác nhận webhook PayOS: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi xác nhận webhook PayOS: " + e.getMessage());
        }
        return ResponseEntity.ok("✅ Webhook đã được xác nhận: " + confirmedUrl);
    }

//    @Hidden
//    @Operation(summary = "Webhook từ PayOS", description = "Endpoint nhận dữ liệu webhook từ PayOS khi trạng thái thanh toán thay đổi. **KHÔNG GỌI TRỰC TIẾP.**")
//    @PostMapping("/webhook/payos")
//    public ObjectNode handleWebhook(@RequestBody ObjectNode body) throws JsonProcessingException {
//        return paymentService.handlePayosWebhook(body);
//    }

//    @Operation(
//            summary = "Tạo link thanh toán PayOS",
//            description = "API này nhận thông tin đơn hàng và số tiền, sau đó trả về một đường link thanh toán của PayOS.",
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    description = "Dữ liệu cần thiết để tạo link thanh toán.",
//                    required = true,
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = CreatePaymentRequestDTO.class),
//                            examples = @ExampleObject(
//                                    value = """
//                                            {
//                                              "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//                                              "amount": 10000,
//                                              "description": "Thanh toan don hang #123"
//                                            }
//                                            """
//                            )
//                    )
//            ),
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "Tạo link thành công",
//                            content = @Content(
//                                    mediaType = "application/json",
//                                    schema = @Schema(implementation = DataResponseDTO.class)
//                            )
//                    )
//            }
//    )
//    @PostMapping("/create")
//    public ResponseEntity<DataResponseDTO<PaymentLinkResponseDTO>> createPaymentLink(
//            @Valid @RequestBody CreatePaymentRequestDTO request) {
//        PaymentLinkResponseDTO paymentLink = paymentService.createPaymentLink(request);
//        return responseHandler.response(200, "Tạo link thanh toán thành công!", paymentLink);
//    }


    //    @GetMapping("/{orderId}/transactions")
//    @Operation(
//            summary = "Lấy danh sách giao dịch theo orderId",
//            description = "API này trả về danh sách tất cả các lần thanh toán (kể cả retry) theo orderId"
//    )
//    public ResponseEntity<DataResponseDTO<List<PaymentTransactionResponse>>> getTransactionsByOrderId(
//            @PathVariable UUID orderId
//    ) {
//        return ResponseEntity.ok(new DataResponseDTO<>(200, "Danh sách giao dịch theo orderId", paymentService.getTransactionsByOrderId(orderId)));
//    }

//    @Operation(
//            summary = "Thử lại thanh toán",
//            description = "API này cho phép người dùng thử lại một giao dịch đã thất bại hoặc hết hạn. Một link thanh toán mới sẽ được tạo cho cùng một đơn hàng.",
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    description = "ID của giao dịch cần thử lại.",
//                    required = true,
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = RetryPaymentRequestDTO.class),
//                            examples = @ExampleObject(
//                                    value = """
//                                            {
//                                              "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
//                                            }
//                                            """
//                            )
//                    )
//            ),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Tạo link thử lại thành công")
//            }
//    )
//    @PostMapping("/retry")
//    public ResponseEntity<DataResponseDTO<PaymentLinkResponseDTO>> retryPayment(
//            @Valid @RequestBody RetryPaymentRequestDTO request) {
//        PaymentLinkResponseDTO paymentLink = paymentService.retryPayment(request);
//        return responseHandler.response(200, "Tạo link thanh toán thử lại thành công!", paymentLink);
//    }


//    @Operation(
//            summary = "Huỷ tất cả giao dịch đang chờ thanh toán của một đơn hàng",
//            description = "Huỷ toàn bộ các link thanh toán còn hiệu lực (trạng thái PENDING) liên kết với một đơn hàng nhất định",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "Huỷ thành công",
//                            content = @Content(schema = @Schema(implementation = CancelPaymentResponseDTO.class))
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "Không tìm thấy giao dịch nào",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(value = "{\"error\": -1, \"message\": \"Không tìm thấy giao dịch\", \"data\": null}")
//                            )
//                    )
//            }
//    )
//    @PostMapping("/cancel")
//    public ResponseEntity cancelAllPendingTransactions(@RequestBody CancelPaymentRequestDTO request) {
//        CancelPaymentResponseDTO response = paymentService.cancelAllPendingTransactions(request);
//        return ResponseEntity.ok(new DataResponseDTO<>(200, "Cancel payment success", response));
//    }

}