package com.example.onkoreabe.settlement.importer;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 지역인구 카테고리 점수 계산기
 */
@Component
public class PopulationScoreCalculator implements ScoreCalculator {

    @Override
    public double calculateScore(Map<String, String> data) {
        try {
            double youthRatio = parseDouble(data, "youth_population_ratio");
            double youngAdultRatio = parseDouble(data, "young_adult_population_ratio");
            double elderlyRatio = parseDouble(data, "elderly_population_ratio");
            double singleHouseholdRatio = parseDouble(data, "single_household_ratio");

            // 청장년 인구 비율 점수 (높을수록 좋음, 50% 기준 100점)
            double youngAdultScore = Math.min(youngAdultRatio / 50 * 100, 100);

            // 고령 인구 비율 점수 (낮을수록 좋음, 20% 이하면 100점)
            double elderlyScore = Math.max(0, 100 - (elderlyRatio - 10) * 5);

            // 유소년 인구 비율 점수 (적정 비율 15~20%)
            double youthScore = 100 - Math.abs(youthRatio - 17.5) * 3;

            // 1인 가구 비율 점수 (적정 비율 30~35%)
            double singleScore = 100 - Math.abs(singleHouseholdRatio - 32.5) * 2;

            // 가중 평균
            double finalScore = (youngAdultScore * 0.4) + (elderlyScore * 0.3)
                              + (youthScore * 0.2) + (singleScore * 0.1);

            return Math.round(Math.max(0, Math.min(100, finalScore)) * 10) / 10.0;

        } catch (Exception e) {
            return 50.0;
        }
    }

    @Override
    public String generateDescription(Map<String, String> data) {
        double score = calculateScore(data);
        if (score >= 80) {
            return "인구 구성이 매우 균형적입니다";
        } else if (score >= 60) {
            return "인구 구성이 양호합니다";
        } else {
            return "인구 구조 개선이 필요합니다";
        }
    }

    private double parseDouble(Map<String, String> data, String key) {
        try {
            String value = data.get(key);
            return value != null ? Double.parseDouble(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
