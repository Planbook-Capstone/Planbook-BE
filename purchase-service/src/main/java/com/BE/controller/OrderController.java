package com.BE.controller;

import com.BE.enums.StatusEnum;
import com.BE.model.request.CreateOrderRequestDTO;
import com.BE.model.request.UpdateOrderStatusRequestDTO;
import com.BE.model.response.CancelPaymentResponseDTO;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.OrderHistoryResponseDTO;
import com.BE.model.response.OrderResponseDTO;
import com.BE.service.interfaceServices.IOrderService;
import com.BE.utils.AccountUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Order API", description = "API quản lý đơn hàng: tạo, xem chi tiết và lịch sử trạng thái.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@SecurityRequirement(name = "api")
public class OrderController {

    private final IOrderService orderService;
    private final ObjectMapper objectMapper;
    private final AccountUtils accountUtils;

    @Operation(
            summary = "Tạo đơn hàng mới",
            description = "API này cho phép tạo một đơn hàng mới với thông tin `userId`, `packageId` và `amount` (giá trị đơn hàng). Đơn hàng được khởi tạo với trạng thái PENDING.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin đơn hàng cần tạo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Example request",
                                    value = """
                                            {
                                              "packageId": "dd49ce4c-9d7b-4e05-b4d4-baaa2b38da35"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tạo đơn hàng thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<DataResponseDTO<OrderResponseDTO>> createOrder(
            @Valid @RequestBody CreateOrderRequestDTO request
    ) {
        OrderResponseDTO dto = orderService.createOrder(request);
        return ResponseEntity.ok(new DataResponseDTO<>(200, "Tạo đơn hàng thành công", dto));
    }

    @Operation(
            summary = "Lấy thông tin chi tiết đơn hàng",
            description = "API này trả về thông tin chi tiết của đơn hàng dựa trên `orderId`."
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<DataResponseDTO<OrderResponseDTO>> getOrder(
            @PathVariable UUID orderId
    ) {
        OrderResponseDTO dto = orderService.getOrder(orderId);
        return ResponseEntity.ok(new DataResponseDTO<>(200, "Chi tiết đơn hàng", dto));
    }

    @Operation(
            summary = "Xem lịch sử trạng thái đơn hàng",
            description = "API này trả về danh sách các thay đổi trạng thái của đơn hàng theo `orderId`, bao gồm các lần chuyển trạng thái cùng ghi chú kèm theo."
    )
    @GetMapping("/{orderId}/history")
    public ResponseEntity<DataResponseDTO<List<OrderHistoryResponseDTO>>> getOrderHistory(
            @PathVariable UUID orderId
    ) {
        List<OrderHistoryResponseDTO> historyList = orderService.getOrderHistory(orderId);
        return ResponseEntity.ok(new DataResponseDTO<>(200, "Lịch sử trạng thái đơn hàng", historyList));
    }


    @Operation(
            summary = "Cập nhật trạng thái đơn hàng",
            description = "API này cho phép cập nhật trạng thái của đơn hàng (`PENDING`, `SUCCESS`, `FAILED`) cùng với ghi chú lý do (tuỳ chọn).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trạng thái mới của đơn hàng",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateOrderStatusRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Example update status",
                                    value = """
                                            {
                                              "status": "CANCEL, RETRY",
                                              "note": "Thanh toán thành công qua PayOS webhook"
                                            }
                                            """
                            )
                    )
            )
    )
    @PatchMapping("/{orderId}/status")
    public ResponseEntity updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO request
    ) {
        return ResponseEntity.ok(new DataResponseDTO<>(200, "Cập nhật trạng thái đơn hàng thành công", orderService.updateOrderStatus(orderId, request.getStatus(), request.getNote())));
    }

    @Hidden
    @PostMapping("/webhook/payOs")
    public ObjectNode handlePayosWebhook(@RequestBody ObjectNode body) throws JsonProcessingException {
        ObjectNode response = objectMapper.createObjectNode();

        try {
            // Gọi service xử lý webhook (xử lý PaymentTransaction + update Order)
            orderService.handlePaymentResult(body);

            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);
            return response;

        } catch (Exception e) {
            response.put("error", -1);
            response.put("message", "Lỗi hệ thống khi xử lý webhook");
            response.set("data", null);
            return response;
        }
    }

    @Operation(
            summary = "Lấy danh sách đơn hàng có lọc và sắp xếp",
            description = "Lọc theo trạng thái đơn hàng (`StatusEnum`) và `userId` (nếu có), sắp xếp theo `createdAt` hoặc `updatedAt` theo chiều `asc|desc`.",
            parameters = {
                    @Parameter(name = "status", description = "Trạng thái đơn hàng", schema = @Schema(implementation = StatusEnum.class, allowableValues = {"PENDING", "PAID", "FAILED", "CANCELLED", "EXPIRED"})),
                    @Parameter(name = "userId", description = "ID người dùng", schema = @Schema(type = "string", format = "uuid")),
                    @Parameter(name = "packageId", description = "ID gói dịch vụ", schema = @Schema(type = "string", format = "uuid")),
                    @Parameter(name = "sortBy", description = "Sắp xếp theo", schema = @Schema(allowableValues = {"createdAt", "updatedAt"}), example = "createdAt"),
                    @Parameter(name = "sortDirection", description = "Chiều sắp xếp", schema = @Schema(allowableValues = {"asc", "desc"}), example = "desc"),
                    @Parameter(name = "offset", description = "Số trang (bắt đầu từ 1)", schema = @Schema(example = "1")),
                    @Parameter(name = "pageSize", description = "Số phần tử mỗi trang", schema = @Schema(example = "10"))
            }
    )
    @GetMapping
    public ResponseEntity getOrders(
            @RequestParam(required = false) StatusEnum status,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID packageId,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "1") int offset,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(new DataResponseDTO<>(200, "Danh sách đơn hàng", orderService.getOrdersWithFilter(
                status, userId, packageId, offset, pageSize, sortBy, sortDirection
        )));
    }

    @Operation(
            summary = "Huỷ giao dịch thanh toán PayOS bị treo",
            description = "API này cho phép hệ thống chủ động huỷ tất cả các link thanh toán còn hiệu lực (trạng thái PENDING) liên kết với một đơn hàng nếu PayOS không tự callback trạng thái huỷ. Áp dụng khi người dùng thoát giữa chừng, hoặc PayOS không gửi webhook.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Huỷ giao dịch thành công",
                            content = @Content(schema = @Schema(implementation = CancelPaymentResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy giao dịch thanh toán nào đang chờ",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "error": -1,
                                                      "message": "Không tìm thấy giao dịch",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @PatchMapping("/cancel-payment/{orderCode}")
    public ResponseEntity cancelPaymentTransactions(@PathVariable Long orderCode) {
        OrderResponseDTO response = orderService.cancelPaymentTransactions(orderCode);
        return ResponseEntity.ok(new DataResponseDTO<>(200, "Huỷ thanh toán thành công", response));
    }


}
