package com.BE.utils;

import com.BE.model.dto.ScoringConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
@Slf4j
public class ScoringConfigUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert Map to ScoringConfig object
     */
    public ScoringConfig mapToScoringConfig(Map<String, Object> scoringConfigMap) {
        if (scoringConfigMap == null || scoringConfigMap.isEmpty()) {
            return getDefaultScoringConfig();
        }

        try {
            return objectMapper.convertValue(scoringConfigMap, ScoringConfig.class);
        } catch (Exception e) {
            log.warn("Failed to convert scoring config map to object, using default: {}", e.getMessage());
            return getDefaultScoringConfig();
        }
    }

    /**
     * Get default scoring configuration
     */
    public ScoringConfig getDefaultScoringConfig() {
        ScoringConfig config = new ScoringConfig();
        config.setUseStandardScoring(true);
        config.setPart1Score(0.25);
        config.setPart2ScoringType("standard");
        config.setPart2CustomScore(1.0);
        config.setPart2ManualScores(Map.of(
            "1", 0.1,
            "2", 0.25,
            "3", 0.5,
            "4", 1.0
        ));
        config.setPart3Score(0.25);
        return config;
    }

    /**
     * Calculate Part II score based on scoring type
     */
    public double calculatePart2Score(int correctCount, int totalStatements, ScoringConfig config) {
        if (config == null) {
            config = getDefaultScoringConfig();
        }

        String scoringType = config.getPart2ScoringType();
        if (scoringType == null) {
            scoringType = "standard";
        }

        double score = 0.0;

        switch (scoringType.toLowerCase()) {
            case "standard":
                score = calculateStandardPart2Score(correctCount);
                break;
            case "auto":
                score = calculateAutoPart2Score(correctCount, totalStatements, config.getPart2CustomScore());
                break;
            case "manual":
                score = calculateManualPart2Score(correctCount, config.getPart2ManualScores());
                break;
            default:
                log.warn("Unknown Part II scoring type: {}, using standard", scoringType);
                score = calculateStandardPart2Score(correctCount);
        }

        return roundToTwoDecimals(score);
    }

    /**
     * Calculate standard Part II score: 0.1/0.25/0.5/1.0
     */
    private double calculateStandardPart2Score(int correctCount) {
        switch (correctCount) {
            case 1:
                return 0.1;
            case 2:
                return 0.25;
            case 3:
                return 0.5;
            case 4:
                return 1.0;
            default:
                return 0.0;
        }
    }

    /**
     * Calculate auto Part II score: customScore ÷ 4 × correctCount
     */
    private double calculateAutoPart2Score(int correctCount, int totalStatements, Double customScore) {
        if (customScore == null) {
            customScore = 1.0;
        }
        if (totalStatements == 0) {
            return 0.0;
        }
        return (customScore / totalStatements) * correctCount;
    }

    /**
     * Calculate manual Part II score using manual scores mapping
     */
    private double calculateManualPart2Score(int correctCount, Map<String, Double> manualScores) {
        if (manualScores == null || manualScores.isEmpty()) {
            return calculateStandardPart2Score(correctCount);
        }

        String key = String.valueOf(correctCount);
        Double score = manualScores.get(key);
        return score != null ? score : 0.0;
    }

    /**
     * Round score to 2 decimal places
     */
    public double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Round score to 2 decimal places and return as float
     */
    public float roundToTwoDecimalsFloat(double value) {
        return (float) roundToTwoDecimals(value);
    }
}
