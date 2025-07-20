package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "exam_submissions")
@EntityListeners(TimestampEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExamSubmission {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_instance_id", nullable = false)
    private ExamInstance examInstance;
    
    @Column(name = "student_name", nullable = false)
    private String studentName;
    
    @Column(name = "score", nullable = false)
    private Float score;
    
    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;
    
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answers_json", columnDefinition = "JSON", nullable = false)
    private Map<String, Object> answersJson;
    
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;
    
    // Relationship with ExamResultDetail
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamResultDetail> resultDetails;
}
