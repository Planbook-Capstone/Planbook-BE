package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import com.BE.enums.QuestionBankVisibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "question_banks")
@EntityListeners(TimestampEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuestionBank {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_content", nullable = false)
    private Map<String, Object> questionContent;

    @Column(name = "explanation", length = 1000)
    private String explanation;

    @Column(name = "reference_source", length = 300)
    private String referenceSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private QuestionBankVisibility visibility;
    
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;
    
    @Column(name = "updated_by")
    private UUID updatedBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper methods
    

    
    /**
     * Check if question is available for use
     */
    public boolean isAvailable() {
        return this.visibility != null;
    }

    /**
     * Check if question bank is public
     */
    public boolean isPublic() {
        return this.visibility == QuestionBankVisibility.PUBLIC;
    }

    /**
     * Check if question bank is private
     */
    public boolean isPrivate() {
        return this.visibility == QuestionBankVisibility.PRIVATE;
    }
    
    /**
     * Get display name combining type and lesson ID
     */
    public String getDisplayName() {
        return String.format("[%s] Lesson %d Question", questionType.getDescription(), lessonId);
    }
}
