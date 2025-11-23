package com.example.onkoreabe.settlement;

import com.example.onkoreabe.settlement.importer.CsvImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 데이터 임포트 관리 API (관리자용)
 */
@Tag(name = "Data Import", description = "데이터 임포트 API (관리자용)")
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/data-import")
@RequiredArgsConstructor
public class DataImportController {

    private final CsvImportService csvImportService;

    /**
     * CSV 파일 업로드 및 데이터 임포트
     */
    @Operation(
            summary = "CSV 파일 임포트",
            description = "카테고리별 CSV 파일을 업로드하여 자치구 점수 데이터를 임포트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임포트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일 형식", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadCsvFile(
            @Parameter(description = "CSV 파일", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "카테고리 (HOUSING, EDUCATION, SAFETY, POPULATION, CONVENIENCE, WELFARE)", required = true)
            @RequestParam("category") ScoreCategory category
    ) {
        log.info("CSV 파일 업로드 요청: category={}, filename={}", category, file.getOriginalFilename());

        try {
            // 파일 검증
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "파일이 비어있습니다"));
            }

            if (!file.getOriginalFilename().endsWith(".csv")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "CSV 파일만 업로드 가능합니다"));
            }

            // CSV 임포트
            int importedCount = csvImportService.importCsvFile(file, category);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "데이터 임포트 성공");
            response.put("category", category);
            response.put("importedCount", importedCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("CSV 파일 임포트 실패", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "임포트 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 카테고리의 모든 데이터 삭제
     */
    @Operation(
            summary = "카테고리 데이터 삭제",
            description = "특정 카테고리의 모든 자치구 점수 데이터를 삭제합니다."
    )
    @DeleteMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> deleteCategoryData(
            @Parameter(description = "카테고리", required = true)
            @PathVariable ScoreCategory category
    ) {
        log.info("카테고리 데이터 삭제 요청: category={}", category);

        // TODO: 구현 필요
        return ResponseEntity.ok(Map.of("success", true, "message", "삭제 기능 구현 예정"));
    }
}
