package com.BE.model;

import com.BE.enums.AcademicResourceEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.Set;

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

    @Column(name = "url", columnDefinition = "TEXT", nullable = false)
    String url;

    @Enumerated(EnumType.STRING)
    AcademicResourceEnum visibility;

    String createdBy;

    @Column(name = "created_at")
    String createdAt;

    @Column(name = "updated_at")
    String updatedAt;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    Set<ResourceTag> resourceTags ;

}
