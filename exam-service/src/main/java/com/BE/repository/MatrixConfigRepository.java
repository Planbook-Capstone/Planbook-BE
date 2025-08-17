package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.MatrixConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatrixConfigRepository extends JpaRepository<MatrixConfig, Long> {
    List<MatrixConfig> findByStatus(StatusEnum status);

}