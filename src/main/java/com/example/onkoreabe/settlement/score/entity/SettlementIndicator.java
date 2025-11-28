package com.example.onkoreabe.settlement.score.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 정착난이도 개별 지표 정의
 * 각 지표는 특정 카테고리에 속하며 CSV 컬럼명과 매핑됨
 */
@Getter
@RequiredArgsConstructor
public enum SettlementIndicator {

    // 교육 카테고리
    ELEMENTARY_SCHOOLS("초등학교 수", ScoreCategory.EDUCATION, "초등학교 수(개)"),
    MIDDLE_SCHOOLS("중학교 수", ScoreCategory.EDUCATION, "중학교 수 (개)"),
    HIGH_SCHOOLS("고등학교 수", ScoreCategory.EDUCATION, "고등학교 수(개)"),
    PRIVATE_ACADEMIES("사설학원 수", ScoreCategory.EDUCATION, "사설학원 수(개)"),

    // 생활편의 카테고리
    LARGE_MARTS("대형마트 수", ScoreCategory.CONVENIENCE, "대형마트 수 (개)"),
    SHOPPING_CENTERS("쇼핑센터 수", ScoreCategory.CONVENIENCE, "대형쇼핑센터 수"),
    RESTAURANTS("외식시설 수", ScoreCategory.CONVENIENCE, "외식시설 수 (개)"),
    SUBWAY_STATIONS("지하철역 수", ScoreCategory.CONVENIENCE, "지하철역 수("),

    // 안전 카테고리
    TRAFFIC_SAFETY_GRADE("교통안전 등급", ScoreCategory.SAFETY, "교통사고 안전(등급)"),
    CRIME_SAFETY_GRADE("범죄안전 등급", ScoreCategory.SAFETY, "범죄안전(등급)"),
    DISEASE_SAFETY_GRADE("감염병안전 등급", ScoreCategory.SAFETY, "감염병안전(등급)"),
    FIRE_SAFETY_GRADE("화재안전 등급", ScoreCategory.SAFETY, "화재안전(등급)"),
    LIFE_SAFETY_GRADE("생활안전 등급", ScoreCategory.SAFETY, "생활안전(등급)"),
    CCTV_COUNT("CCTV 수", ScoreCategory.SAFETY, "CCTV 설치 현황(개)"),

    // 인구 카테고리
    YOUTH_POPULATION_RATIO("유소년인구 비율", ScoreCategory.POPULATION, "유소년인구 비율(%)"),
    WORKING_AGE_RATIO("청장년인구 비율", ScoreCategory.POPULATION, "청장년인구 비율(%)"),
    ELDERLY_RATIO("고령인구 비율", ScoreCategory.POPULATION, "고령인구 비율(%)"),
    SINGLE_HOUSEHOLD_COUNT("1인 가구 수", ScoreCategory.POPULATION, "1인 가구 비율(%)"),

    // 주택 카테고리
    TOTAL_POPULATION("총 인구 수", ScoreCategory.HOUSING, "총 인구 수"),
    KOREAN_POPULATION("내국인 수", ScoreCategory.HOUSING, "내국인 수"),
    FOREIGN_POPULATION("외국인 수", ScoreCategory.HOUSING, "외국인 수"),
    FOREIGN_HOUSEHOLDS("외국인 가구 수", ScoreCategory.HOUSING, "외국인 가구 수"),
    TOTAL_HOUSES("총 주택 수", ScoreCategory.HOUSING, "주택 수"),
    DETACHED_HOUSES("단독주택 수", ScoreCategory.HOUSING, "단독 주택 수"),
    APARTMENTS("아파트 수", ScoreCategory.HOUSING, "아파트 수"),
    ROW_HOUSES("연립주택 수", ScoreCategory.HOUSING, "연립주택 수"),
    MULTI_FAMILY_HOUSES("다세대주택 수", ScoreCategory.HOUSING, "다세대 주택 수"),
    MONTHLY_RENT_PRICE("월세 상대가격", ScoreCategory.HOUSING, "월세 상대 가격"),
    JEONSE_PRICE("전세 상대가격", ScoreCategory.HOUSING, "전세 상대 가격"),
    SALE_PRICE("매매 상대가격", ScoreCategory.HOUSING, "매매 상대 가격"),

    // 복지 카테고리
    KINDERGARTENS("유치원 수", ScoreCategory.WELFARE, "유치원 수"),
    CHILDCARE_CENTERS("보육시설 수", ScoreCategory.WELFARE, "보육시설 수"),
    HOSPITALS("병의원 수", ScoreCategory.WELFARE, "병의원 수 "),
    PHARMACIES("약국 수", ScoreCategory.WELFARE, "약국 수"),
    VETERINARY_HOSPITALS("동물병원 수", ScoreCategory.WELFARE, "동물병원 수"),
    ELDERLY_WELFARE_FACILITIES("노인복지시설 수", ScoreCategory.WELFARE, "노인복지시설 수"),
    SOCIAL_WELFARE_FACILITIES("사회복지시설 수", ScoreCategory.WELFARE, "사회복지시설 수"),
    DISABLED_WELFARE_FACILITIES("장애인복지시설 수", ScoreCategory.WELFARE, "장애인복지시설 수"),
    SPORTS_FACILITIES("체육시설 수", ScoreCategory.WELFARE, "체육시설 수"),
    CULTURAL_FACILITIES("문화시설 수", ScoreCategory.WELFARE, "문화시설 수 ");

    private final String displayName;
    private final ScoreCategory category;
    private final String csvColumnName;
}
