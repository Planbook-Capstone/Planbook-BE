package com.BE.mapper;

import com.BE.model.entity.PaymentTransaction;
import com.BE.model.response.PaymentLinkResponseDTO;
import com.BE.model.response.PaymentTransactionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    PaymentTransactionResponse toResponse(PaymentTransaction entity);

    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "checkoutUrl", target = "checkoutUrl")
    @Mapping(target = "orderCode", ignore = true) // Sẽ được set trong service
    PaymentLinkResponseDTO toPaymentLinkResponseDTO(PaymentTransaction entity);
}
