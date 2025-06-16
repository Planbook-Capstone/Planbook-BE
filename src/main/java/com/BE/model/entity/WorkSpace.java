package com.BE.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;
import com.BE.model.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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
    @JoinColumn(name = "account_id")
    private User user;


    String createdAt;
    String updatedAt;

    // Getters and setters
}