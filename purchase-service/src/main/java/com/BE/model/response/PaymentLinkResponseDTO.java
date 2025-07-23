package com.BE.model.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class PaymentLinkResponseDTO {
    private String checkoutUrl;
    private UUID paymentId;
    private long orderCode; // PayOS order code
}