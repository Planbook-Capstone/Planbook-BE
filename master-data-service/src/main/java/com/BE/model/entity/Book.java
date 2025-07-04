package com.BE.model.entity;


import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {
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
    @JoinColumn(name = "subject_id")
    @JsonIgnore
    Subject subject;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<Chapter> chapters = new HashSet<>();
}
