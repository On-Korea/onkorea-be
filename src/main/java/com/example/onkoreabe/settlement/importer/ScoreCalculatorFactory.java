package com.example.onkoreabe.settlement.importer;

import com.example.onkoreabe.settlement.ScoreCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 카테고리별 점수 계산기 팩토리
 */
@Component
@RequiredArgsConstructor
public class ScoreCalculatorFactory {

    private final HousingScoreCalculator housingCalculator;
    private final EducationScoreCalculator educationCalculator;
    private final SafetyScoreCalculator safetyCalculator;
    private final PopulationScoreCalculator populationCalculator;
    private final ConvenienceScoreCalculator convenienceCalculator;
    private final WelfareScoreCalculator welfareCalculator;

    private Map<ScoreCategory, ScoreCalculator> calculators;

    public ScoreCalculator getCalculator(ScoreCategory category) {
        if (calculators == null) {
            initCalculators();
        }

        ScoreCalculator calculator = calculators.get(category);
        if (calculator == null) {
            throw new IllegalArgumentException("해당 카테고리의 계산기가 없습니다: " + category);
        }
        return calculator;
    }

    private void initCalculators() {
        calculators = new HashMap<>();
        calculators.put(ScoreCategory.HOUSING, housingCalculator);
        calculators.put(ScoreCategory.EDUCATION, educationCalculator);
        calculators.put(ScoreCategory.SAFETY, safetyCalculator);
        calculators.put(ScoreCategory.POPULATION, populationCalculator);
        calculators.put(ScoreCategory.CONVENIENCE, convenienceCalculator);
        calculators.put(ScoreCategory.WELFARE, welfareCalculator);
    }
}
