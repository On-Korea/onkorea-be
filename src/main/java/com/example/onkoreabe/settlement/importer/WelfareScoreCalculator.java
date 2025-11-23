package com.example.onkoreabe.settlement.importer;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 복지문화 카테고리 점수 계산기
 */
@Component
public class WelfareScoreCalculator implements ScoreCalculator {

    @Override
    public double calculateScore(Map<String, String> data) {
        try {
            double kindergartens = parseDouble(data, "kindergartens");
            double childcareFacilities = parseDouble(data, "childcare_facilities");
            double hospitalsClinics = parseDouble(data, "hospitals_clinics");
            double pharmacies = parseDouble(data, "pharmacies");
            double animalHospitals = parseDouble(data, "animal_hospitals");
            double elderlyWelfare = parseDouble(data, "elderly_welfare_facilities");
            double socialWelfare = parseDouble(data, "social_welfare_facilities");
            double culturalFacilities = parseDouble(data, "cultural_facilities");

            // 아동 복지 점수
            double childcareScore = Math.min((kindergartens + childcareFacilities) / 250 * 100, 100);

            // 의료 시설 점수
            double healthcareScore = Math.min((hospitalsClinics + pharmacies + animalHospitals) / 450 * 100, 100);

            // 노인 및 사회 복지 점수
            double welfareScore = Math.min((elderlyWelfare + socialWelfare) / 75 * 100, 100);

            // 문화 시설 점수
            double cultureScore = Math.min(culturalFacilities / 45 * 100, 100);

            // 가중 평균
            double finalScore = (childcareScore * 0.25) + (healthcareScore * 0.35)
                              + (welfareScore * 0.25) + (cultureScore * 0.15);

            return Math.round(finalScore * 10) / 10.0;

        } catch (Exception e) {
            return 50.0;
        }
    }

    @Override
    public String generateDescription(Map<String, String> data) {
        double score = calculateScore(data);
        if (score >= 80) {
            return "복지 및 문화 인프라가 매우 우수합니다";
        } else if (score >= 60) {
            return "복지 문화 시설이 양호합니다";
        } else {
            return "복지 문화 시설 확충이 필요합니다";
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
