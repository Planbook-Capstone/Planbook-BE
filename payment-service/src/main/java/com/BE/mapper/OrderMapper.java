package com.BE.mapper;

import com.BE.model.entity.Order;
import com.BE.model.entity.OrderHistory;
import com.BE.model.entity.PaymentTransaction;
import com.BE.model.response.OrderHistoryResponseDTO;
import com.BE.model.response.OrderResponseDTO;
import com.BE.model.response.PaymentLinkResponseDTO;
import com.BE.model.response.PaymentTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDTO toOrderResponseDTO(Order order);
    @Mapping(source = "order.id", target = "orderId")
    OrderHistoryResponseDTO toOrderHistoryResponseDTO(OrderHistory history);

    @Mapping(source = "order.id", target = "orderId")
    PaymentTransactionResponse toPaymentTransactionResponse(PaymentTransaction transaction);




}
