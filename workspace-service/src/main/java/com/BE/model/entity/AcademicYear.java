package com.BE.model.entity;

import com.BE.enums.AcademicYearStatusEnum;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AcademicYear {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String yearLabel;

    @Enumerated(EnumType.STRING)
    private AcademicYearStatusEnum status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL)
    private Set<WorkSpace> workSpaces = new HashSet<>();

}