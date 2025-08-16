package com.BE.model.entity;

import com.BE.config.MapToJsonConverter;
import com.BE.config.TimestampEntityListener;
import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "slide_template")
@EntityListeners(TimestampEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    StatusEnum status;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Column(columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> textBlocks;

    @Column(columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> imageBlocks;


    LocalDateTime createdAt;


    LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "slideTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<SlideDetail> slideDetails;

}
