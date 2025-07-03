package com.BE.model.request;



import com.BE.enums.Status;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusRequest {

    @Schema(example = "ACTIVE, INACTIVE", description = "Status Enum")
    @EnumValidator(enumClass = Status.class, message = "Invalid status value")
    @Enumerated(EnumType.STRING)
    Status status;

}

