package com.BE.model.entity;

import com.BE.enums.StatusEnum;
import com.BE.enums.ToolTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

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
    @GeneratedValue
    @UuidGenerator
    UUID id;


    String name;
    String apiUrl;
    String tokenUrl;
    String clientId;
    String clientSecret;

    Integer tokenCostPerQuery;

    @Enumerated(EnumType.STRING)
    ToolTypeEnum toolType;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Column(columnDefinition = "json")
    String inputJson;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    UUID createdBy;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;


}
