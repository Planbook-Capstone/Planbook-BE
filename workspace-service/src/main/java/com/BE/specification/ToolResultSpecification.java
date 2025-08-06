package com.BE.specification;

import com.BE.enums.ToolResultSource;
import com.BE.enums.ToolResultStatus;
import com.BE.enums.ToolResultType;
import com.BE.model.entity.ToolResult;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification class để build dynamic queries cho ToolResult
 */
public class ToolResultSpecification {

    /**
     * Tạo Specification để filter ToolResult theo nhiều điều kiện - Đã đơn giản hóa
     */
    public static Specification<ToolResult> buildSpecification(
            UUID userId,
            Long academicYearId,
            ToolResultType type,
            ToolResultStatus status,
            ToolResultSource source,
            Long templateId,
            String nameContains,
            String descriptionContains,
            List<Long> lessonIds
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter theo userId
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }

            // Filter theo workspaceId
            if (academicYearId != null) {
                predicates.add(criteriaBuilder.equal(root.get("academicYearId"), academicYearId));
            }

            // Filter theo type
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            // Filter theo status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (source != null) {
                predicates.add(criteriaBuilder.equal(root.get("source"), source));
            }

            // Filter theo templateId
            if (templateId != null) {
                predicates.add(criteriaBuilder.equal(root.get("templateId"), templateId));
            }

            // Filter theo name (contains)
            if (nameContains != null && !nameContains.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + nameContains.toLowerCase() + "%"
                ));
            }

            // Filter theo description (contains)
            if (descriptionContains != null && !descriptionContains.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + descriptionContains.toLowerCase() + "%"
                ));
            }

            // ✅ Lọc lessonIds chính xác bằng JSON_CONTAINS
            if (lessonIds != null && !lessonIds.isEmpty()) {
                List<Predicate> lessonPredicates = lessonIds.stream()
                        .map(id -> criteriaBuilder.isTrue(criteriaBuilder.function(
                                "JSON_CONTAINS",
                                Boolean.class,
                                root.get("lessonIds"),
                                criteriaBuilder.literal(id.toString()),
                                criteriaBuilder.literal("$")
                        )))
                        .toList();

                predicates.add(criteriaBuilder.or(lessonPredicates.toArray(new Predicate[0])));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
