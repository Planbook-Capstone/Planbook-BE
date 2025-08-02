package com.BE.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcademicResourceResponse {
    
    Long id;
    Long lessonId;
    String type;
    String name;
    String description;
    String url;
    String visibility;
    UUID createdBy;
    String createdAt;
    String updatedAt;
    Set<TagResponse> tags;
}
