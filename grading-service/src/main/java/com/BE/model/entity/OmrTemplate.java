package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.enums.StatusEnum;
import com.BE.exception.EnumValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing OMR template for exam sheets
 */
@Entity
@Table(name = "omr_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@Builder
public class OmrTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false, unique = true , columnDefinition = "TEXT")
    private String name;

    @Column(name = "sample_image_url", length = 500)
    private String sampleImageUrl;

    @OneToOne(mappedBy = "omrTemplate", fetch = FetchType.LAZY)
    @JsonIgnore
    private GradingSession gradingSession;


    @Enumerated(EnumType.STRING)
    private StatusEnum status = StatusEnum.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
