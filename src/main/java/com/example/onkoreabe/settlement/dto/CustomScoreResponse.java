package com.example.onkoreabe.settlement.dto;

import com.example.onkoreabe.settlement.SeoulDistrict;
import com.example.onkoreabe.settlement.SettlementIndicator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 사용자 맞춤 점수 계산 응답
 */
@Getter
@Builder
@Schema(description = "사용자 맞춤 점수 계산 응답")
public class CustomScoreResponse {

    @Schema(description = "자치구별 맞춤 점수 목록")
    private List<DistrictCustomScore> districts;

    @Schema(description = "선택된 지표 목록")
    private List<IndicatorInfo> selectedIndicators;

    @Schema(description = "서울시 평균 점수")
    private Double averageScore;

    @Getter
    @Builder
    @Schema(description = "자치구별 맞춤 점수")
    public static class DistrictCustomScore {
        @Schema(description = "자치구 코드")
        private SeoulDistrict districtCode;

        @Schema(description = "자치구명")
        private String districtName;

        @Schema(description = "위도")
        private Double latitude;

        @Schema(description = "경도")
        private Double longitude;

        @Schema(description = "맞춤 종합 점수 (0-100)")
        private Double totalScore;

        @Schema(description = "지표별 상세 점수")
        private Map<SettlementIndicator, Double> indicatorScores;
    }

    @Getter
    @Builder
    @Schema(description = "선택된 지표 정보")
    public static class IndicatorInfo {
        @Schema(description = "지표 코드")
        private SettlementIndicator indicator;

        @Schema(description = "지표 이름")
        private String displayName;

        @Schema(description = "소속 카테고리")
        private String category;
    }
}
