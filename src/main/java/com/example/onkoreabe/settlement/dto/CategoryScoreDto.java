package com.example.onkoreabe.settlement.dto;

import com.example.onkoreabe.settlement.ScoreCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카테고리별 점수 DTO
 */
@Schema(description = "카테고리별 점수")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryScoreDto {

    @Schema(description = "카테고리", example = "HOUSING")
    private ScoreCategory category;

    @Schema(description = "카테고리 표시명", example = "주택")
    private String categoryName;

    @Schema(description = "점수 (0~100)", example = "85.5")
    private Double score;

    @Schema(description = "점수 설명", example = "주거 환경이 우수하며 주택 가격이 적정합니다")
    private String description;
}
