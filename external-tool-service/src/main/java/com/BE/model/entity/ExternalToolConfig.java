package com.BE.model.entity;

import com.BE.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "external_tool_configs")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalToolConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String apiUrl;
    String tokenUrl;
    String clientId;
    String clientSecret;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    UUID createdBy;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;


}
