package com.BE.model.entity;

import com.BE.enums.StatusEnum;
import com.BE.enums.ToolCodeEnum;
import com.BE.enums.ToolTypeEnum;
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

    String href;

    int priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    ToolCodeEnum code;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String icon;

    @Enumerated(EnumType.STRING)
    ToolTypeEnum toolType;


    @Column(nullable = false) // Đảm bảo không null và có giá trị mặc định
    Integer tokenCostPerQuery;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    String createdAt;

    String updatedAt;

}
