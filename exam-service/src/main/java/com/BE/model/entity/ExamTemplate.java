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
@Table(name = "exam_templates")
@EntityListeners(TimestampEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExamTemplate {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "subject", nullable = false)
    private String subject;
    
    @Column(name = "grade", nullable = false)
    private Integer grade;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "school")
    private String school;

    @Column(name = "exam_code")
    private String examCode;

    @Column(name = "atomic_masses")
    private String atomicMasses;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_json", nullable = false)
    private Map<String, Object> contentJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scoring_config")
    private Map<String, Object> scoringConfig;

    @Column(name = "total_score")
    private Double totalScore = 10.0;

    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationship with ExamInstance
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamInstance> examInstances;
}
