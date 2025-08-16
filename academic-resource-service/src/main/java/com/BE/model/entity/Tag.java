package com.BE.model.entity;
import com.BE.config.TimestampEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "name", length = 100, nullable = false, unique = true)
    String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    UUID createdBy;
    
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    Set<ResourceTag> resourceTags;
}
