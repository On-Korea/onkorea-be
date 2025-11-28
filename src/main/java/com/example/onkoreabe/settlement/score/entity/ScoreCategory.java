package com.example.onkoreabe.settlement.score.entity;

/**
 * 정착난이도 스코어 카테고리
 */
public enum ScoreCategory {
    HOUSING("주택", "주거 환경 및 주택 가격"),
    POPULATION("지역인구", "인구 구성 및 밀도"),
    SAFETY("안전", "범죄율 및 안전 수준"),
    EDUCATION("교육", "교육 시설 및 환경"),
    CONVENIENCE("생활편의교통", "교통 및 생활 편의 시설"),
    WELFARE("복지문화", "복지 및 문화 시설");

    private final String displayName;
    private final String description;

    ScoreCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
