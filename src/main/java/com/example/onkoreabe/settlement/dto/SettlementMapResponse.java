package com.example.onkoreabe.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 정착난이도 스코어맵 전체 응답 DTO
 */
@Schema(description = "정착난이도 스코어맵 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementMapResponse {

    @Schema(description = "총 자치구 수", example = "25")
    private Integer totalDistricts;

    @Schema(description = "자치구별 점수 목록")
    private List<DistrictScoreResponse> districts;

    @Schema(description = "서울시 평균 점수", example = "75.5")
    private Double averageScore;
}
