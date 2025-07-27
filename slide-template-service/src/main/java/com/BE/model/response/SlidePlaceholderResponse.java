package com.BE.model.response;

import com.BE.enums.PlaceholderTypeEnum;
import com.BE.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlidePlaceholderResponse {
    long id;
    PlaceholderTypeEnum type;
    String name;
    String description;
    StatusEnum status;
    String createdAt;
    String updatedAt;
}
