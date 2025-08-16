package com.BE.model.entity;

import com.BE.config.MapToJsonConverter;
import com.BE.config.TimestampEntityListener;
import com.BE.enums.SubscriptionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "subscription_package")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(TimestampEntityListener.class)
public class SubscriptionPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(nullable = false)
    Integer tokenAmount;

    @Column(nullable = false)
    BigDecimal price = BigDecimal.ZERO;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Column
    boolean highlight = false;

    Integer priority = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SubscriptionStatus status;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "subscriptionPackage", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    Set<Order> orders = new HashSet<>();

    @Column(columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> features;


}