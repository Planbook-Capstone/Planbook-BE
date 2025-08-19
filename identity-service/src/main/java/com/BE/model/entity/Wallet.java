package com.BE.model.entity;

import com.BE.config.TimestampEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(TimestampEntityListener.class)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    Integer balance = 0;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    User user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    @Builder.Default
    Set<WalletTransaction> transactions = new HashSet<>();

    public void addUser(User account){
        this.setUser(account);
        account.setWallet(this);
    }

}

