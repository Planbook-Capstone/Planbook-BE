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

/**
 * Entity representing student submission with graded answers in JSON format
 */
@Entity
@Table(name = "student_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@Builder
public class StudentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grading_session_id", nullable = false)
    private GradingSession gradingSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_sheet_key_id", nullable = false)
    private AnswerSheetKey answerSheetKey;

    @Column(name = "student_code", nullable = false, length = 50)
    private String studentCode;

    @Column(name = "exam_code", nullable = false, length = 50)
    private String examCode;

    @Column(name = "image_base64", columnDefinition = "LONGTEXT")
    private String imageBase64;

    @Column(name = "score", nullable = false)
    private Float score;

    @Column(name = "total_correct", nullable = false)
    private Integer totalCorrect;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "student_answer_json", columnDefinition = "TEXT")
    private JsonNode studentAnswerJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
