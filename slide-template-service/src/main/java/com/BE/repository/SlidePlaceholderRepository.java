package com.BE.repository;

import com.BE.model.entity.SlidePlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SlidePlaceholderRepository extends JpaRepository<SlidePlaceholder, Long>, JpaSpecificationExecutor<SlidePlaceholder> {
}
