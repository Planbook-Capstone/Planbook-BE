package com.BE.model.entity;

import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    String createdAt;
    String updatedAt;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    @JsonIgnore
    Chapter chapter;

}
