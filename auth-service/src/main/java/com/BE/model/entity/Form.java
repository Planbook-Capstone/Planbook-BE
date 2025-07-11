package com.BE.model.entity;

import com.BE.enums.FormStatusEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "form_name", nullable = false)
    String name;

    @Column(name = "form_status", nullable = false)
    FormStatusEnum status;
    
    @Column(name = "form_description")
    String description;

    @Column(name = "form_definition", columnDefinition = "json")
    String formDefinition;

    String createdAt;
    String updatedAt;

}
