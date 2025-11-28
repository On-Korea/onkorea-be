package com.example.onkoreabe.settlement.dto;

import com.example.onkoreabe.settlement.score.entity.SettlementIndicator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 사용자 맞춤 점수 계산 요청
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "사용자 맞춤 점수 계산 요청")
public class CustomScoreRequest {

    @Schema(description = "사용자가 선택한 지표 목록", example = "[\"ELEMENTARY_SCHOOLS\", \"CCTV_COUNT\", \"HOSPITALS\"]")
    private List<SettlementIndicator> indicators;
}
