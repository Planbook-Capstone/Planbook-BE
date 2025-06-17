package com.BE.model.entity;

import com.BE.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookType {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    String name;

    String description;

    int priority;
    
    @Column(nullable = false) // Đảm bảo không null và có giá trị mặc định
    Integer tokenCostPerQuery;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String icon;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    String createdAt;

    String updatedAt;

}
