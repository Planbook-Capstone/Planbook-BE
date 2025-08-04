package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.convert.LongListToJsonConverter;
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
import java.util.ArrayList;
import java.util.List;
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = LongListToJsonConverter.class)
    List<Long> lessonIds;
    
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
     * Check if this question bank belongs to a specific lesson
     */
    public boolean belongsToLesson(Long lessonId) {
        return lessonIds != null && lessonIds.contains(lessonId);
    }

    /**
     * Add a lesson ID to this question bank
     */
    public void addLessonId(Long lessonId) {
        if (lessonIds == null) {
            lessonIds = new ArrayList<>();
        }
        if (!lessonIds.contains(lessonId)) {
            lessonIds.add(lessonId);
        }
    }

    /**
     * Remove a lesson ID from this question bank
     */
    public void removeLessonId(Long lessonId) {
        if (lessonIds != null) {
            lessonIds.remove(lessonId);
        }
    }

    /**
     * Get the number of lessons this question bank belongs to
     */
    public int getLessonCount() {
        return lessonIds != null ? lessonIds.size() : 0;
    }


    public boolean isPublic() {
        return this.visibility == QuestionBankVisibility.PUBLIC;
    }


    public boolean isPrivate() {
        return this.visibility == QuestionBankVisibility.PRIVATE;
    }


}
