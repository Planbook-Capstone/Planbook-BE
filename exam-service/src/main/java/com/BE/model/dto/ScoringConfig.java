package com.BE.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoringConfig {
    
    private Boolean useStandardScoring;
    private Double part1Score;
    private String part2ScoringType; // "standard", "auto", "manual"
    private Double part2CustomScore;
    private Map<String, Double> part2ManualScores; // Map of correctCount -> score
    private Double part3Score;
}
