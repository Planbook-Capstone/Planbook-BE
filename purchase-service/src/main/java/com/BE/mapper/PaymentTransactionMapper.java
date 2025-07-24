package com.BE.mapper;

import com.BE.model.entity.PaymentTransaction;
import com.BE.model.response.PaymentTransactionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    PaymentTransactionResponse toResponse(PaymentTransaction entity);

}
