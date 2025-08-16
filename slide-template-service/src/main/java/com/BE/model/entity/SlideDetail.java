package com.BE.model.entity;

import com.BE.config.MapToJsonConverter;
import com.BE.config.TimestampEntityListener;
import com.BE.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "slide_detail")
@EntityListeners(TimestampEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideDetail {
    @Id
    String id; // Sử dụng id từ JSON (ví dụ: "p1", "p2")

    @Column(nullable = false)
    String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String slideData; // Lưu toàn bộ JSON của slide

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description; // Mô tả placeholder: "1 LessonName, 1 LessonDescription, 1 CreatedDate"

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    StatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_template_id", nullable = false)
    SlideTemplate slideTemplate;


    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
