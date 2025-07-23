package com.BE.model.response;

import com.BE.model.entity.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderHistoryResponseDTO {
    private UUID id;
    private UUID orderId;
    private String fromStatus;
    private String toStatus;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
