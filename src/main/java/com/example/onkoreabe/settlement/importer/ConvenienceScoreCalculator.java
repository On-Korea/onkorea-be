package com.example.onkoreabe.settlement.importer;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 생활편의교통 카테고리 점수 계산기
 */
@Component
public class ConvenienceScoreCalculator implements ScoreCalculator {

    @Override
    public double calculateScore(Map<String, String> data) {
        try {
            double convenienceFacilities = parseDouble(data, "convenience_facilities");
            double departmentStores = parseDouble(data, "department_stores");
            double largeMarts = parseDouble(data, "large_marts");
            double convenienceStores = parseDouble(data, "convenience_stores_supermarkets");
            double groceryStores = parseDouble(data, "grocery_stores");
            double restaurants = parseDouble(data, "restaurants");
            double publicTransport = parseDouble(data, "public_transport_facilities");
            double petServices = parseDouble(data, "pet_service_facilities");

            // 대중교통 점수 (50개 기준 100점)
            double transportScore = Math.min(publicTransport / 50 * 100, 100);

            // 쇼핑 시설 점수
            double shoppingScore = Math.min((departmentStores * 10 + largeMarts * 5 + convenienceStores / 10), 100);

            // 생활 편의 시설 점수
            double facilitiesScore = Math.min((convenienceFacilities / 15 + groceryStores / 2 + restaurants / 35), 100);

            // 반려동물 서비스 점수
            double petScore = Math.min(petServices / 120 * 100, 100);

            // 가중 평균
            double finalScore = (transportScore * 0.4) + (shoppingScore * 0.3)
                              + (facilitiesScore * 0.25) + (petScore * 0.05);

            return Math.round(finalScore * 10) / 10.0;

        } catch (Exception e) {
            return 50.0;
        }
    }

    @Override
    public String generateDescription(Map<String, String> data) {
        double score = calculateScore(data);
        if (score >= 80) {
            return "생활 편의 시설과 교통이 매우 우수합니다";
        } else if (score >= 60) {
            return "생활하기 편리한 환경입니다";
        } else {
            return "편의 시설 확충이 필요합니다";
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
