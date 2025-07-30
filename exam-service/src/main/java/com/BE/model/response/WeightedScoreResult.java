package com.BE.model.response;

/**
 * Result class for weighted score calculation
 */
public class WeightedScoreResult {
    private final float score;
    private final float maxScore;

    public WeightedScoreResult(float score, float maxScore) {
        this.score = score;
        this.maxScore = maxScore;
    }

    public float getScore() {
        return score;
    }

    public float getMaxScore() {
        return maxScore;
    }
}
