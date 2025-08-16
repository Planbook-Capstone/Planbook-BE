package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.BE.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    UUID orderId;

    @Column(nullable = false)
    Integer tokenBefore; // Số token trước khi nạp

    @Column(nullable = false)
    Integer tokenChange; // Số token được nạp thêm (hoặc thưởng, hoặc hoàn)

    @Enumerated(EnumType.STRING)
    TransactionType type; // Nạp hoặc hoàn

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;

    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @JsonIgnore
    Wallet wallet;


    public void addWallet(Wallet wa) {
        this.setWallet(wa);
        wa.getTransactions().add(this);
    }


}

