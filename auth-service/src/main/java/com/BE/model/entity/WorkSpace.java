package com.BE.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class WorkSpace {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "auth_id")
    private AuthUser auth;


    String createdAt;
    String updatedAt;

    // Getters and setters
}