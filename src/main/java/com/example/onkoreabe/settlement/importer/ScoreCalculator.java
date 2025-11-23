package com.example.onkoreabe.settlement.importer;

import java.util.Map;

/**
 * 점수 계산 인터페이스
 */
public interface ScoreCalculator {

    /**
     * CSV 데이터를 기반으로 0-100점 사이의 점수 계산
     *
     * @param data CSV 레코드 데이터 (컬럼명 -> 값)
     * @return 계산된 점수 (0.0 ~ 100.0)
     */
    double calculateScore(Map<String, String> data);

    /**
     * 점수에 대한 설명 생성
     *
     * @param data CSV 레코드 데이터
     * @return 설명 문자열
     */
    String generateDescription(Map<String, String> data);
}
