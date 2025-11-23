package com.example.onkoreabe.settlement.importer;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 안전 카테고리 점수 계산기
 */
@Component
public class SafetyScoreCalculator implements ScoreCalculator {

    @Override
    public double calculateScore(Map<String, String> data) {
        try {
            // 안전등급은 1~5등급 (1이 가장 좋음)
            double trafficGrade = parseDouble(data, "traffic_safety_grade");
            double crimeGrade = parseDouble(data, "crime_safety_grade");
            double diseaseGrade = parseDouble(data, "infectious_disease_safety_grade");
            double fireGrade = parseDouble(data, "fire_safety_grade");
            double lifeGrade = parseDouble(data, "life_safety_grade");
            double cctvCount = parseDouble(data, "cctv_count");

            // 등급을 점수로 변환 (1등급=100점, 5등급=20점)
            double gradeAvg = (trafficGrade + crimeGrade + diseaseGrade + fireGrade + lifeGrade) / 5.0;
            double gradeScore = 100 - ((gradeAvg - 1) * 20);

            // CCTV 점수 (많을수록 좋음, 5000개 기준 100점)
            double cctvScore = Math.min(cctvCount / 5000 * 100, 100);

            // 가중 평균 (등급 80%, CCTV 20%)
            double finalScore = (gradeScore * 0.8) + (cctvScore * 0.2);

            return Math.round(finalScore * 10) / 10.0;

        } catch (Exception e) {
            return 50.0;
        }
    }

    @Override
    public String generateDescription(Map<String, String> data) {
        double score = calculateScore(data);
        if (score >= 80) {
            return "매우 안전한 지역입니다";
        } else if (score >= 60) {
            return "전반적으로 안전한 편입니다";
        } else {
            return "안전 개선이 필요합니다";
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
