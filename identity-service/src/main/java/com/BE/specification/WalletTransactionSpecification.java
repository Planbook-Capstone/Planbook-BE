package com.BE.specification;

import com.BE.model.entity.WalletTransaction;
import com.BE.model.request.WalletTransactionFilterRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class WalletTransactionSpecification {

    public static Specification<WalletTransaction> build(WalletTransactionFilterRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUserId() != null) {
                Join<Object, Object> walletJoin = root.join("wallet");
                Join<Object, Object> userJoin = walletJoin.join("user");
                predicates.add(cb.equal(userJoin.get("id"), request.getUserId()));
            }

            if (request.getType() != null) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }

            if (request.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getFromDate().atStartOfDay()));
            }

            if (request.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getToDate().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
