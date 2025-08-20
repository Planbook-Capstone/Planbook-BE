package com.BE.repository;



import com.BE.enums.GatewayEnum;
import com.BE.enums.StatusEnum;
import com.BE.model.entity.Order;
import com.BE.model.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findByPayosTransactionId(String payosTransactionId);

    Optional<PaymentTransaction> findByPayosOrderCode(Long payosOrderCode);

    Optional<PaymentTransaction> findByParentTransactionId(UUID parentTransactionId);

    @Query("SELECT p FROM PaymentTransaction p WHERE p.order.id = :orderId AND p.parentTransactionId IS NULL")
    Optional<PaymentTransaction> findRootTransactionByOrderId(@Param("orderId") UUID orderId);

    boolean existsByOrderAndGatewayAndStatusIn(Order order, GatewayEnum gateway, List<StatusEnum> statuses);


    List<PaymentTransaction> findAllByOrderId(UUID orderId);


    boolean existsByOrderAndGatewayAndStatus(Order order, GatewayEnum gateway, StatusEnum status);




}
