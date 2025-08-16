package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false)
    UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatusEnum status;

    @Column(nullable = false)
    BigDecimal amount = BigDecimal.ZERO;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    Set<OrderHistory> orderHistories = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    Set<PaymentTransaction> transactions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    SubscriptionPackage subscriptionPackage;

    public void addHistory(OrderHistory history) {
        orderHistories.add(history);
        history.setOrder(this);
    }

    public void addTransaction(PaymentTransaction txn) {
        transactions.add(txn);
        txn.setOrder(this);
    }

    public void addSubcriptionPackage(SubscriptionPackage subscriptionPackage) {
        subscriptionPackage.getOrders().add(this);
        this.setSubscriptionPackage(subscriptionPackage);
    }
}