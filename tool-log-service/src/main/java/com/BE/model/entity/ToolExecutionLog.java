package com.BE.model.entity;


import com.BE.config.MapToJsonConverter;
import com.BE.enums.ExecutionStatus;
import com.BE.enums.ToolTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tool_usage_logs")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToolExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    UUID userId;

    @Column(nullable = false)
    UUID toolId; // Có thể là ID của ExternalToolConfig hoặc BookType

    Long lessonId;

    @Column(nullable = false)
    String toolName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ExecutionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ToolTypeEnum toolType;

    @Column(nullable = false)
    Integer tokenUsed;

    @Column(columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> input;

    @Column(columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> output;


//
//    @Column(nullable = false)
//    Boolean success; // true nếu gọi thành công
//
//    @Column(columnDefinition = "TEXT")
//    String errorMessage;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

}

