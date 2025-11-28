package com.example.onkoreabe.settlement;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자가 선택한 지표들로 맞춤 점수 계산
 * 선택된 지표들에 동일 가중치를 부여하여 계산
 */
@Component
public class CustomScoreCalculator {

    /**
     * 선택된 지표들을 기반으로 점수 계산
     *
     * @param indicators 사용자가 선택한 지표 목록
     * @param rawData CSV에서 읽은 원시 데이터 (컬럼명 -> 값)
     * @return 0-100 사이의 점수
     */
    public double calculateScore(List<SettlementIndicator> indicators, Map<String, String> rawData) {
        if (indicators == null || indicators.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        int validCount = 0;

        for (SettlementIndicator indicator : indicators) {
            double indicatorScore = calculateIndicatorScore(indicator, rawData);
            totalScore += indicatorScore;
            validCount++;
        }

        // 동일 가중치로 평균 계산
        double finalScore = validCount > 0 ? totalScore / validCount : 0.0;
        return Math.round(finalScore * 10) / 10.0;
    }

    /**
     * 개별 지표의 점수 계산 (0-100점)
     */
    private double calculateIndicatorScore(SettlementIndicator indicator, Map<String, String> rawData) {
        String csvColumn = indicator.getCsvColumnName();
        double rawValue = parseDouble(rawData.get(csvColumn));

        // 지표별 점수 계산 로직
        switch (indicator) {
            // 교육: 시설 수 기반 (많을수록 좋음)
            case ELEMENTARY_SCHOOLS:
            case MIDDLE_SCHOOLS:
            case HIGH_SCHOOLS:
                return normalizeSchoolCount(rawValue);
            case PRIVATE_ACADEMIES:
                return normalizeAcademyCount(rawValue);

            // 생활편의: 시설 수 기반
            case LARGE_MARTS:
                return normalizeMartCount(rawValue);
            case SHOPPING_CENTERS:
                return normalizeShoppingCenterCount(rawValue);
            case RESTAURANTS:
                return normalizeRestaurantCount(rawValue);
            case SUBWAY_STATIONS:
                return normalizeSubwayCount(rawValue);

            // 안전: 등급 기반 (낮을수록 좋음, 1등급=100점)
            case TRAFFIC_SAFETY_GRADE:
            case CRIME_SAFETY_GRADE:
            case DISEASE_SAFETY_GRADE:
            case FIRE_SAFETY_GRADE:
            case LIFE_SAFETY_GRADE:
                return normalizeSafetyGrade(rawValue);
            case CCTV_COUNT:
                return normalizeCctvCount(rawValue);

            // 인구: 비율 기반
            case YOUTH_POPULATION_RATIO:
                return normalizeYouthRatio(rawValue);
            case WORKING_AGE_RATIO:
                return normalizeWorkingAgeRatio(rawValue);
            case ELDERLY_RATIO:
                return normalizeElderlyRatio(rawValue);
            case SINGLE_HOUSEHOLD_COUNT:
                return normalizeSingleHouseholdCount(rawValue);

            // 주택: 인구 및 주택 수
            case TOTAL_POPULATION:
                return normalizePopulation(rawValue);
            case TOTAL_HOUSES:
                return normalizeHouseCount(rawValue);
            case APARTMENTS:
                return normalizeApartmentCount(rawValue);
            case MONTHLY_RENT_PRICE:
            case JEONSE_PRICE:
            case SALE_PRICE:
                return normalizeHousingPrice(rawValue);

            // 복지: 시설 수 기반
            case KINDERGARTENS:
            case CHILDCARE_CENTERS:
                return normalizeChildcareCount(rawValue);
            case HOSPITALS:
                return normalizeHospitalCount(rawValue);
            case PHARMACIES:
                return normalizePharmacyCount(rawValue);
            case VETERINARY_HOSPITALS:
                return normalizeVetCount(rawValue);
            case ELDERLY_WELFARE_FACILITIES:
            case SOCIAL_WELFARE_FACILITIES:
            case DISABLED_WELFARE_FACILITIES:
                return normalizeWelfareCount(rawValue);
            case SPORTS_FACILITIES:
            case CULTURAL_FACILITIES:
                return normalizeCulturalCount(rawValue);

            default:
                return 50.0; // 기본값
        }
    }

    // ==================== 정규화 함수들 ====================

    private double normalizeSchoolCount(double count) {
        // 학교 수: 0~50개 기준
        return Math.min(count / 50 * 100, 100);
    }

    private double normalizeAcademyCount(double count) {
        // 학원 수: 0~2000개 기준
        return Math.min(count / 2000 * 100, 100);
    }

    private double normalizeMartCount(double count) {
        // 대형마트: 0~6개 기준
        return Math.min(count / 6 * 100, 100);
    }

    private double normalizeShoppingCenterCount(double count) {
        // 쇼핑센터: 0~12개 기준
        return Math.min(count / 12 * 100, 100);
    }

    private double normalizeRestaurantCount(double count) {
        // 외식시설: 0~16000개 기준
        return Math.min(count / 16000 * 100, 100);
    }

    private double normalizeSubwayCount(double count) {
        // 지하철역: 0~27개 기준
        return Math.min(count / 27 * 100, 100);
    }

    private double normalizeSafetyGrade(double grade) {
        // 안전 등급: 1(최고)~5(최저)
        if (grade == 0) return 50.0; // 데이터 없음
        return 100 - ((grade - 1) * 20);
    }

    private double normalizeCctvCount(double count) {
        // CCTV: 0~1500개 기준
        return Math.min(count / 1500 * 100, 100);
    }

    private double normalizeYouthRatio(double ratio) {
        // 유소년 비율: 높을수록 좋음 (0~15% 기준)
        return Math.min(ratio / 15 * 100, 100);
    }

    private double normalizeWorkingAgeRatio(double ratio) {
        // 청장년 비율: 높을수록 좋음 (60~80% 이상적)
        return Math.min(ratio / 80 * 100, 100);
    }

    private double normalizeElderlyRatio(double ratio) {
        // 고령인구 비율: 낮을수록 좋음 (30% 기준으로 역산)
        return Math.max(0, 100 - (ratio / 30 * 100));
    }

    private double normalizeSingleHouseholdCount(double count) {
        // 1인 가구 수: 중립적 지표 (데이터 분포 기준)
        // 0~150000 기준
        return Math.min(count / 150000 * 100, 100);
    }

    private double normalizePopulation(double population) {
        // 인구: 중립적 (적당한 인구가 이상적, 300000~500000 기준)
        if (population >= 300000 && population <= 500000) {
            return 100.0;
        } else if (population < 300000) {
            return population / 300000 * 100;
        } else {
            return Math.max(0, 100 - ((population - 500000) / 200000 * 50));
        }
    }

    private double normalizeHouseCount(double count) {
        // 주택 수: 많을수록 좋음 (0~250000 기준)
        return Math.min(count / 250000 * 100, 100);
    }

    private double normalizeApartmentCount(double count) {
        // 아파트 수: 많을수록 좋음 (0~170000 기준)
        return Math.min(count / 170000 * 100, 100);
    }

    private double normalizeHousingPrice(double relativePrice) {
        // 상대가격: 1.0 기준, 낮을수록 좋음 (0.95~1.10 범위)
        if (relativePrice <= 1.0) {
            return 100.0;
        } else {
            // 1.0 초과시 감점 (1.10 이상이면 0점)
            return Math.max(0, 100 - ((relativePrice - 1.0) / 0.10 * 100));
        }
    }

    private double normalizeChildcareCount(double count) {
        // 유치원/보육시설: 0~300개 기준
        return Math.min(count / 300 * 100, 100);
    }

    private double normalizeHospitalCount(double count) {
        // 병의원: 0~3600개 기준
        return Math.min(count / 3600 * 100, 100);
    }

    private double normalizePharmacyCount(double count) {
        // 약국: 0~510개 기준
        return Math.min(count / 510 * 100, 100);
    }

    private double normalizeVetCount(double count) {
        // 동물병원: 0~100개 기준
        return Math.min(count / 100 * 100, 100);
    }

    private double normalizeWelfareCount(double count) {
        // 복지시설: 0~220개 기준
        return Math.min(count / 220 * 100, 100);
    }

    private double normalizeCulturalCount(double count) {
        // 체육/문화시설: 0~240개 기준
        return Math.min(count / 240 * 100, 100);
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            // 쉼표 제거
            value = value.replace(",", "").replace("\"", "").trim();
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
