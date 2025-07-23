package com.BE.utils;

import com.BE.model.dto.ScoringConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScoringConfigUtilsTest {

    @InjectMocks
    private ScoringConfigUtils scoringConfigUtils;

    private ScoringConfig testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new ScoringConfig();
        testConfig.setUseStandardScoring(false);
        testConfig.setPart1Score(0.25);
        testConfig.setPart2ScoringType("manual");
        testConfig.setPart2CustomScore(4.0);
        
        Map<String, Double> manualScores = new HashMap<>();
        manualScores.put("1", 0.1);
        manualScores.put("2", 0.25);
        manualScores.put("3", 3.0);
        manualScores.put("4", 10.0);
        testConfig.setPart2ManualScores(manualScores);
        testConfig.setPart3Score(0.25);
    }

    @Test
    void testCalculatePart2Score_StandardScoring() {
        ScoringConfig standardConfig = new ScoringConfig();
        standardConfig.setPart2ScoringType("standard");

        assertEquals(0.0, scoringConfigUtils.calculatePart2Score(0, 4, standardConfig));
        assertEquals(0.1, scoringConfigUtils.calculatePart2Score(1, 4, standardConfig));
        assertEquals(0.25, scoringConfigUtils.calculatePart2Score(2, 4, standardConfig));
        assertEquals(0.5, scoringConfigUtils.calculatePart2Score(3, 4, standardConfig));
        assertEquals(1.0, scoringConfigUtils.calculatePart2Score(4, 4, standardConfig));
    }

    @Test
    void testCalculatePart2Score_AutoScoring() {
        ScoringConfig autoConfig = new ScoringConfig();
        autoConfig.setPart2ScoringType("auto");
        autoConfig.setPart2CustomScore(2.0);

        assertEquals(0.0, scoringConfigUtils.calculatePart2Score(0, 4, autoConfig));
        assertEquals(0.5, scoringConfigUtils.calculatePart2Score(1, 4, autoConfig));
        assertEquals(1.0, scoringConfigUtils.calculatePart2Score(2, 4, autoConfig));
        assertEquals(1.5, scoringConfigUtils.calculatePart2Score(3, 4, autoConfig));
        assertEquals(2.0, scoringConfigUtils.calculatePart2Score(4, 4, autoConfig));
    }

    @Test
    void testCalculatePart2Score_ManualScoring() {
        assertEquals(0.0, scoringConfigUtils.calculatePart2Score(0, 4, testConfig));
        assertEquals(0.1, scoringConfigUtils.calculatePart2Score(1, 4, testConfig));
        assertEquals(0.25, scoringConfigUtils.calculatePart2Score(2, 4, testConfig));
        assertEquals(3.0, scoringConfigUtils.calculatePart2Score(3, 4, testConfig));
        assertEquals(10.0, scoringConfigUtils.calculatePart2Score(4, 4, testConfig));
    }

    @Test
    void testRoundToTwoDecimals() {
        assertEquals(1.23, scoringConfigUtils.roundToTwoDecimals(1.234));
        assertEquals(1.24, scoringConfigUtils.roundToTwoDecimals(1.235));
        assertEquals(1.0, scoringConfigUtils.roundToTwoDecimals(1.0));
        assertEquals(0.33, scoringConfigUtils.roundToTwoDecimals(1.0/3.0));
    }

    @Test
    void testMapToScoringConfig() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("useStandardScoring", false);
        configMap.put("part1Score", 0.25);
        configMap.put("part2ScoringType", "manual");
        configMap.put("part2CustomScore", 4.0);
        
        Map<String, Double> manualScores = new HashMap<>();
        manualScores.put("1", 0.1);
        manualScores.put("2", 0.25);
        manualScores.put("3", 3.0);
        manualScores.put("4", 10.0);
        configMap.put("part2ManualScores", manualScores);
        configMap.put("part3Score", 0.25);

        ScoringConfig result = scoringConfigUtils.mapToScoringConfig(configMap);

        assertNotNull(result);
        assertEquals(false, result.getUseStandardScoring());
        assertEquals(0.25, result.getPart1Score());
        assertEquals("manual", result.getPart2ScoringType());
        assertEquals(4.0, result.getPart2CustomScore());
        assertEquals(0.25, result.getPart3Score());
        assertEquals(4, result.getPart2ManualScores().size());
        assertEquals(0.1, result.getPart2ManualScores().get("1"));
        assertEquals(10.0, result.getPart2ManualScores().get("4"));
    }

    @Test
    void testGetDefaultScoringConfig() {
        ScoringConfig defaultConfig = scoringConfigUtils.getDefaultScoringConfig();

        assertNotNull(defaultConfig);
        assertTrue(defaultConfig.getUseStandardScoring());
        assertEquals(0.25, defaultConfig.getPart1Score());
        assertEquals("standard", defaultConfig.getPart2ScoringType());
        assertEquals(1.0, defaultConfig.getPart2CustomScore());
        assertEquals(0.25, defaultConfig.getPart3Score());
        assertNotNull(defaultConfig.getPart2ManualScores());
        assertEquals(4, defaultConfig.getPart2ManualScores().size());
    }
}
