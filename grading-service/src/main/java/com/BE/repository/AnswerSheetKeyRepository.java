package com.BE.repository;

import com.BE.model.entity.AnswerSheetKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerSheetKeyRepository extends JpaRepository<AnswerSheetKey, Long> {
    Optional<AnswerSheetKey> findByGradingSessionIdAndCode(Long gradingSessionId, String code);
    List<AnswerSheetKey> findByGradingSessionId(Long gradingSessionId);
}

