package com.BE.model.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPackageRequest {

    @NotBlank(message = "Package name is required")
    String name;

    @NotNull(message = "Token amount is required")
    @Min(value = 1, message = "Token amount must be at least 1")
    Integer tokenAmount;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price;

    String description;
}
