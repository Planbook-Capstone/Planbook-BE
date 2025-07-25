package com.BE.model.entity;

import com.BE.config.MapToJsonConverter;
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
@Table(name = "slide_template")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    StatusEnum status;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Column(columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> textBlocks;

    @Column(columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> imageBlocks;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

}
