package com.BE.service.interfaceServices;

import com.BE.enums.StatusEnum;
import com.BE.model.request.CreateOrderRequestDTO;
import com.BE.model.response.OrderHistoryResponseDTO;
import com.BE.model.response.OrderResponseDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IOrderService {
    OrderResponseDTO createOrder(CreateOrderRequestDTO request);
    OrderResponseDTO getOrder(UUID orderId);
    List<OrderHistoryResponseDTO> getOrderHistory(UUID orderId);
    OrderResponseDTO updateOrderStatus(UUID orderId, StatusEnum newStatus, String note);
    void handlePaymentResult(ObjectNode body) throws Exception;

    Page<OrderResponseDTO> getOrdersWithFilter(StatusEnum status, UUID userId, UUID packageId,
                                               int offset, int pageSize,
                                               String sortBy, String sortDirection);
    OrderResponseDTO cancelPaymentTransactions(Long orderCode);
}
