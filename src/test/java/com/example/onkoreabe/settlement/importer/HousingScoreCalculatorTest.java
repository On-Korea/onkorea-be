package com.example.onkoreabe.settlement.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HousingScoreCalculator 단위 테스트
 */
@DisplayName("HousingScoreCalculator 테스트")
class HousingScoreCalculatorTest {

    private HousingScoreCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new HousingScoreCalculator();
    }

    @Test
    @DisplayName("주택 점수 계산 - 높은 점수")
    void calculateScore_HighScore() {
        // given
        Map<String, String> data = new HashMap<>();
        data.put("total_population", "100000");
        data.put("total_houses", "50000");
        data.put("monthly_rent_relative_price", "90");
        data.put("jeonse_relative_price", "95");
        data.put("sale_relative_price", "100");

        // when
        double score = calculator.calculateScore(data);

        // then
        assertThat(score).isGreaterThanOrEqualTo(0);
        assertThat(score).isLessThanOrEqualTo(100);
        assertThat(score).isGreaterThan(80); // 좋은 조건이므로 높은 점수 예상
    }

    @Test
    @DisplayName("주택 점수 계산 - 낮은 점수")
    void calculateScore_LowScore() {
        // given
        Map<String, String> data = new HashMap<>();
        data.put("total_population", "100000");
        data.put("total_houses", "20000"); // 주택 부족
        data.put("monthly_rent_relative_price", "150"); // 비싼 가격
        data.put("jeonse_relative_price", "160");
        data.put("sale_relative_price", "170");

        // when
        double score = calculator.calculateScore(data);

        // then
        assertThat(score).isGreaterThanOrEqualTo(0);
        assertThat(score).isLessThanOrEqualTo(100);
        assertThat(score).isLessThan(60); // 나쁜 조건이므로 낮은 점수 예상
    }

    @Test
    @DisplayName("설명 생성 - 높은 점수")
    void generateDescription_HighScore() {
        // given
        Map<String, String> data = new HashMap<>();
        data.put("total_population", "100000");
        data.put("total_houses", "50000");
        data.put("monthly_rent_relative_price", "90");
        data.put("jeonse_relative_price", "95");
        data.put("sale_relative_price", "100");

        // when
        String description = calculator.generateDescription(data);

        // then
        assertThat(description).contains("우수");
    }

    @Test
    @DisplayName("설명 생성 - 낮은 점수")
    void generateDescription_LowScore() {
        // given
        Map<String, String> data = new HashMap<>();
        data.put("total_population", "100000");
        data.put("total_houses", "20000");
        data.put("monthly_rent_relative_price", "150");
        data.put("jeonse_relative_price", "160");
        data.put("sale_relative_price", "170");

        // when
        String description = calculator.generateDescription(data);

        // then
        assertThat(description).contains("개선");
    }
}
