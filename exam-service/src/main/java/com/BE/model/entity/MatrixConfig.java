package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.convert.JsonNodeConverter;
import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "matrix_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatrixConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    String description;

    @Convert(converter = JsonNodeConverter.class)
    @Column(columnDefinition = "json")
    JsonNode matrixJson;

    @Enumerated(EnumType.STRING)
    StatusEnum status = StatusEnum.INACTIVE;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
