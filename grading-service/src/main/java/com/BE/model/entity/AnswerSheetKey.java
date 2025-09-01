package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.utils.JsonNodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing answer sheet key with correct answers in JSON format
 */
@Entity
@Table(name = "answer_sheet_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@Builder
public class AnswerSheetKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grading_session_id", nullable = false)
    private GradingSession gradingSession;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "answer_json", columnDefinition = "TEXT")
    private JsonNode answerJson;

    @OneToMany(mappedBy = "answerSheetKey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentSubmission> studentSubmissions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
