package com.BE.repository;

import com.BE.model.entity.ToolExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ToolExecutionLogRepository extends JpaRepository<ToolExecutionLog, Long>, JpaSpecificationExecutor<ToolExecutionLog> {
}
