package com.BE.model.entity;

import com.BE.enums.PlaceholderTypeEnum;
import com.BE.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "slide_placeholder")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlidePlaceholder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    PlaceholderTypeEnum type;

    @Column(nullable = false)
    String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    StatusEnum status;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
