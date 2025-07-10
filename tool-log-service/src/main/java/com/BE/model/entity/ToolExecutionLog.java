package com.BE.model.entity;


import com.BE.enums.ToolTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
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
    Long toolId; // Có thể là ID của ExternalToolConfig hoặc BookType

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ToolTypeEnum toolType;

    Integer tokenUsed;

    @Column(columnDefinition = "json") // Nếu DB hỗ trợ
    String input;

    @Column(columnDefinition = "json")
    String output;
//
//    @Column(nullable = false)
//    Boolean success; // true nếu gọi thành công
//
//    @Column(columnDefinition = "TEXT")
//    String errorMessage;

    @CreationTimestamp
    LocalDateTime createdAt;

}

