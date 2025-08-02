package com.BE.model.entity;

import com.BE.enums.AcademicResourceEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "academic_resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcademicResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "type", length = 20)
    String type; // image, gif, video, webp, iframe

    @Column(name = "name", length = 255)
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    Long lessonId;

    @Column(name = "url", columnDefinition = "TEXT", nullable = false)
    String url;

    @Enumerated(EnumType.STRING)
    AcademicResourceEnum visibility;

    UUID createdBy;

    @Column(name = "created_at")
    @CreationTimestamp
    String createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    String updatedAt;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    Set<ResourceTag> resourceTags;

}
