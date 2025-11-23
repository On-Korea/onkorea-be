package com.example.onkoreabe.settlement.importer;

import com.example.onkoreabe.settlement.ScoreCategory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 주택 카테고리 점수 계산기
 */
@Component
public class HousingScoreCalculator implements ScoreCalculator {

    @Override
    public double calculateScore(Map<String, String> data) {
        try {
            double totalPopulation = parseDouble(data, "total_population");
            double totalHouses = parseDouble(data, "total_houses");
            double monthlyRentPrice = parseDouble(data, "monthly_rent_relative_price");
            double jeonsePrice = parseDouble(data, "jeonse_relative_price");
            double salePrice = parseDouble(data, "sale_relative_price");

            // 주택 보급률 (인구 대비 주택 수)
            double housingSupplyRatio = (totalHouses / (totalPopulation / 2.5)) * 100;  // 2.5인 가구 가정
            double housingScore = Math.min(housingSupplyRatio, 100.0);

            // 가격 점수 (낮을수록 좋음, 100 기준으로 역산)
            double avgPrice = (monthlyRentPrice + jeonsePrice + salePrice) / 3.0;
            double priceScore = Math.max(0, 100 - (avgPrice - 100));  // 100을 기준으로 차이만큼 감점

            // 가중 평균 (주택 보급률 40%, 가격 점수 60%)
            double finalScore = (housingScore * 0.4) + (priceScore * 0.6);

            return Math.round(finalScore * 10) / 10.0;

        } catch (Exception e) {
            return 50.0;  // 오류 시 기본 점수
        }
    }

    @Override
    public String generateDescription(Map<String, String> data) {
        double score = calculateScore(data);
        if (score >= 80) {
            return "주거 환경이 매우 우수하며 주택 가격이 적정합니다";
        } else if (score >= 60) {
            return "주거 환경이 양호하나 가격 부담이 있을 수 있습니다";
        } else {
            return "주택 공급 및 가격 개선이 필요합니다";
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

    /**
     * ScoreCalculatorFactory에서 사용할 수 있도록 카테고리 매핑 제공
     */
    public static Map<ScoreCategory, ScoreCalculator> createMapping(HousingScoreCalculator calculator) {
        Map<ScoreCategory, ScoreCalculator> map = new HashMap<>();
        map.put(ScoreCategory.HOUSING, calculator);
        return map;
    }
}
