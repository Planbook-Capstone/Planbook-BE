package com.BE.model.entity;

import com.BE.enums.OrderStatusEnum;
import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
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
public class Order {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID packageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;

    @Column(nullable = false)
    BigDecimal amount = BigDecimal.ZERO;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    Set<OrderHistory> orderHistories = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    Set<PaymentTransaction> transactions = new HashSet<>();

    public void addHistory(OrderHistory history) {
        orderHistories.add(history);
        history.setOrder(this);
    }

    public void addTransaction(PaymentTransaction txn) {
        transactions.add(txn);
        txn.setOrder(this);
    }
}