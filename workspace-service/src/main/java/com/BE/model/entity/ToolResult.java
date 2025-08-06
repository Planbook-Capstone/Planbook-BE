package com.BE.model.entity;

import com.BE.convert.LongListToJsonConverter;
import com.BE.convert.ObjectToJsonConverter;
import com.BE.enums.ToolResultSource;
import com.BE.enums.ToolResultStatus;
import com.BE.enums.ToolResultType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tool_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToolResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    UUID userId;

    @Column(nullable = false)
    Long academicYearId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ToolResultType type; // Ví dụ: LESSON_PLAN, SLIDE, EXAM,...

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ToolResultSource source;

    @Column
    Long templateId; // Nếu có, dùng để truy ngược Template

    @Column(nullable = false)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "json")
    @Convert(converter = ObjectToJsonConverter.class)
    Object data;

    @Column(columnDefinition = "json")
    @Convert(converter = LongListToJsonConverter.class)
    List<Long> lessonIds;

    @Enumerated(EnumType.STRING)
    ToolResultStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
