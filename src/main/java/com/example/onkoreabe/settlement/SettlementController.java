package com.example.onkoreabe.settlement;

import com.example.onkoreabe.settlement.dto.DistrictScoreResponse;
import com.example.onkoreabe.settlement.dto.SettlementMapResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 정착난이도 스코어맵 API 컨트롤러
 */
@Tag(name = "Settlement", description = "정착난이도 스코어맵 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * 전체 자치구의 정착난이도 점수 조회
     */
    @Operation(
            summary = "전체 자치구 점수 조회",
            description = "서울시 25개 자치구의 정착난이도 점수를 조회합니다. 모든 카테고리의 점수가 포함됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SettlementMapResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping
    public ResponseEntity<SettlementMapResponse> getAllDistrictScores() {
        log.info("전체 자치구 점수 조회 요청");
        SettlementMapResponse response = settlementService.getAllDistrictScores();
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 카테고리 기준으로 자치구 점수 조회
     */
    @Operation(
            summary = "카테고리별 자치구 점수 조회",
            description = "특정 카테고리(주택, 안전, 교육 등) 기준으로 자치구별 점수를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SettlementMapResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 카테고리", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<SettlementMapResponse> getDistrictScoresByCategory(
            @Parameter(description = "카테고리 (HOUSING, POPULATION, SAFETY, EDUCATION, CONVENIENCE, WELFARE)", required = true)
            @PathVariable ScoreCategory category
    ) {
        log.info("카테고리별 자치구 점수 조회 요청: category={}", category);
        SettlementMapResponse response = settlementService.getDistrictScoresByCategory(category);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 자치구의 상세 점수 조회
     */
    @Operation(
            summary = "자치구 상세 점수 조회",
            description = "특정 자치구의 모든 카테고리별 점수를 상세 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = DistrictScoreResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 자치구 코드", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/district/{district}")
    public ResponseEntity<DistrictScoreResponse> getDistrictDetail(
            @Parameter(description = "자치구 코드 (GANGNAM, SONGPA 등)", required = true)
            @PathVariable SeoulDistrict district
    ) {
        log.info("자치구 상세 점수 조회 요청: district={}", district);
        DistrictScoreResponse response = settlementService.getDistrictDetail(district);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용 가능한 카테고리 목록 조회
     */
    @Operation(
            summary = "카테고리 목록 조회",
            description = "사용 가능한 모든 카테고리 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/categories")
    public ResponseEntity<ScoreCategory[]> getCategories() {
        log.info("카테고리 목록 조회 요청");
        return ResponseEntity.ok(ScoreCategory.values());
    }

    /**
     * 자치구 목록 조회
     */
    @Operation(
            summary = "자치구 목록 조회",
            description = "서울시 25개 자치구 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/districts")
    public ResponseEntity<SeoulDistrict[]> getDistricts() {
        log.info("자치구 목록 조회 요청");
        return ResponseEntity.ok(SeoulDistrict.values());
    }
}
