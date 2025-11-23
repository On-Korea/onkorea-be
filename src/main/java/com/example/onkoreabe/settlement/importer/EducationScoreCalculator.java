package com.example.onkoreabe.settlement.importer;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 교육 카테고리 점수 계산기
 */
@Component
public class EducationScoreCalculator implements ScoreCalculator {

    @Override
    public double calculateScore(Map<String, String> data) {
        try {
            double elementarySchools = parseDouble(data, "elementary_schools");
            double middleSchools = parseDouble(data, "middle_schools");
            double highSchools = parseDouble(data, "high_schools");
            double privateAcademies = parseDouble(data, "private_academies");

            // 학교 수 점수 정규화 (서울 평균 기준)
            double schoolScore = Math.min((elementarySchools + middleSchools + highSchools) / 100 * 100, 100);

            // 사설학원 수 점수 정규화 (많을수록 교육 인프라 좋음)
            double academyScore = Math.min(privateAcademies / 2000 * 100, 100);

            // 가중 평균 (학교 70%, 학원 30%)
            double finalScore = (schoolScore * 0.7) + (academyScore * 0.3);

            return Math.round(finalScore * 10) / 10.0;

        } catch (Exception e) {
            return 50.0;
        }
    }

    @Override
    public String generateDescription(Map<String, String> data) {
        double score = calculateScore(data);
        if (score >= 80) {
            return "교육 인프라가 매우 우수합니다";
        } else if (score >= 60) {
            return "교육 환경이 양호합니다";
        } else {
            return "교육 시설 확충이 필요합니다";
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
