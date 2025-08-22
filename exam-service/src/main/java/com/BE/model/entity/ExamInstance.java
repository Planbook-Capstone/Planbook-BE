package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.enums.ExamInstanceStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exam_instances")
@EntityListeners(TimestampEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExamInstance {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ExamTemplate template;
    
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
    
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;
    
    @Column(name = "excel_url")
    private String excelUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExamInstanceStatus status = ExamInstanceStatus.SCHEDULED;

    @Column(name = "status_changed_at")
    private LocalDateTime statusChangedAt;

    @Column(name = "status_change_reason")
    private String statusChangeReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship with ExamSubmission
    @OneToMany(mappedBy = "examInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamSubmission> submissions;
}
