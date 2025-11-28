package com.example.onkoreabe.settlement.dto;

import com.example.onkoreabe.settlement.score.entity.SeoulDistrict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 자치구별 점수 응답 DTO
 */
@Schema(description = "자치구별 점수 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictScoreResponse {

    @Schema(description = "자치구 코드", example = "GANGNAM")
    private SeoulDistrict districtCode;

    @Schema(description = "자치구명", example = "강남구")
    private String districtName;

    @Schema(description = "위도", example = "37.5172")
    private Double latitude;

    @Schema(description = "경도", example = "127.0473")
    private Double longitude;

    @Schema(description = "종합 점수 (전체 카테고리 평균)", example = "82.3")
    private Double totalScore;

    @Schema(description = "카테고리별 점수 목록")
    private List<CategoryScoreDto> categoryScores;
}
