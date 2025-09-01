package com.BE.repository;

import com.BE.model.entity.GradingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GradingSessionRepository extends JpaRepository<GradingSession, Long> {
    List<GradingSession> findByBookTypeId(UUID bookTypeId);
}

