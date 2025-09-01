package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.utils.JsonNodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a grading session
 */
@Entity
@Table(name = "grading_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@Builder
public class GradingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "book_type_id", nullable = false)
    private UUID bookTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "omr_template_id", nullable = false)
    private OmrTemplate omrTemplate;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "section_config_json", columnDefinition = "json")
    private JsonNode sectionConfigJson;

    @OneToMany(mappedBy = "gradingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnswerSheetKey> answerSheetKeys;

    @OneToMany(mappedBy = "gradingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentSubmission> studentSubmissions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}