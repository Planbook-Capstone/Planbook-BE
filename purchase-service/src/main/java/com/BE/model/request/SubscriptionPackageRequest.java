package com.BE.model.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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


    @NotNull(message = "Feature list is required")
    @Size(min = 1, message = "At least one feature is required")
    @Builder.Default
    List<@NotBlank(message = "Feature description must not be blank") String> features = new ArrayList<>();
}
