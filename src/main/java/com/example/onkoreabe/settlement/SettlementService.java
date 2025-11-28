package com.example.onkoreabe.settlement;

import com.example.onkoreabe.settlement.dto.CategoryScoreDto;
import com.example.onkoreabe.settlement.dto.CustomScoreResponse;
import com.example.onkoreabe.settlement.dto.DistrictScoreResponse;
import com.example.onkoreabe.settlement.dto.SettlementMapResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 정착난이도 스코어맵 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final DistrictScoreRepository districtScoreRepository;
    private final DistrictRawDataRepository rawDataRepository;
    private final CustomScoreCalculator customScoreCalculator;

    /**
     * 전체 자치구의 점수 조회
     */
    @Transactional(readOnly = true)
    public SettlementMapResponse getAllDistrictScores() {
        List<DistrictScore> allScores = districtScoreRepository.findAll();

        // 자치구별로 그룹화
        Map<SeoulDistrict, List<DistrictScore>> scoresByDistrict = allScores.stream()
                .collect(Collectors.groupingBy(DistrictScore::getDistrict));

        // 각 자치구의 응답 생성
        List<DistrictScoreResponse> districtResponses = Arrays.stream(SeoulDistrict.values())
                .map(district -> buildDistrictResponse(district, scoresByDistrict.get(district)))
                .sorted(Comparator.comparing(DistrictScoreResponse::getTotalScore).reversed())
                .collect(Collectors.toList());

        // 서울시 평균 점수 계산
        double averageScore = districtResponses.stream()
                .mapToDouble(DistrictScoreResponse::getTotalScore)
                .average()
                .orElse(0.0);

        return SettlementMapResponse.builder()
                .totalDistricts(SeoulDistrict.values().length)
                .districts(districtResponses)
                .averageScore(Math.round(averageScore * 10) / 10.0)
                .build();
    }

    /**
     * 특정 카테고리 기준으로 자치구 점수 조회
     */
    @Transactional(readOnly = true)
    public SettlementMapResponse getDistrictScoresByCategory(ScoreCategory category) {
        List<DistrictScore> categoryScores = districtScoreRepository.findByCategory(category);

        Map<SeoulDistrict, DistrictScore> scoreMap = categoryScores.stream()
                .collect(Collectors.toMap(DistrictScore::getDistrict, score -> score));

        List<DistrictScoreResponse> districtResponses = Arrays.stream(SeoulDistrict.values())
                .map(district -> {
                    DistrictScore score = scoreMap.get(district);
                    CategoryScoreDto categoryScore = score != null ?
                            buildCategoryScoreDto(score) :
                            buildEmptyCategoryScoreDto(category);

                    return DistrictScoreResponse.builder()
                            .districtCode(district)
                            .districtName(district.getDisplayName())
                            .latitude(district.getLatitude())
                            .longitude(district.getLongitude())
                            .totalScore(score != null ? score.getScore() : 0.0)
                            .categoryScores(List.of(categoryScore))
                            .build();
                })
                .sorted(Comparator.comparing(DistrictScoreResponse::getTotalScore).reversed())
                .collect(Collectors.toList());

        double averageScore = districtResponses.stream()
                .mapToDouble(DistrictScoreResponse::getTotalScore)
                .average()
                .orElse(0.0);

        return SettlementMapResponse.builder()
                .totalDistricts(SeoulDistrict.values().length)
                .districts(districtResponses)
                .averageScore(Math.round(averageScore * 10) / 10.0)
                .build();
    }

    /**
     * 특정 자치구의 상세 점수 조회
     */
    @Transactional(readOnly = true)
    public DistrictScoreResponse getDistrictDetail(SeoulDistrict district) {
        List<DistrictScore> scores = districtScoreRepository.findByDistrict(district);
        return buildDistrictResponse(district, scores);
    }

    /**
     * 자치구 응답 생성
     */
    private DistrictScoreResponse buildDistrictResponse(SeoulDistrict district, List<DistrictScore> scores) {
        List<CategoryScoreDto> categoryScores = scores != null ?
                scores.stream()
                        .map(this::buildCategoryScoreDto)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        double totalScore = categoryScores.stream()
                .mapToDouble(CategoryScoreDto::getScore)
                .average()
                .orElse(0.0);

        return DistrictScoreResponse.builder()
                .districtCode(district)
                .districtName(district.getDisplayName())
                .latitude(district.getLatitude())
                .longitude(district.getLongitude())
                .totalScore(Math.round(totalScore * 10) / 10.0)
                .categoryScores(categoryScores)
                .build();
    }

    /**
     * 카테고리 점수 DTO 생성
     */
    private CategoryScoreDto buildCategoryScoreDto(DistrictScore score) {
        return CategoryScoreDto.builder()
                .category(score.getCategory())
                .categoryName(score.getCategory().getDisplayName())
                .score(score.getScore())
                .description(score.getDescription())
                .build();
    }

    /**
     * 빈 카테고리 점수 DTO 생성
     */
    private CategoryScoreDto buildEmptyCategoryScoreDto(ScoreCategory category) {
        return CategoryScoreDto.builder()
                .category(category)
                .categoryName(category.getDisplayName())
                .score(0.0)
                .description("데이터 없음")
                .build();
    }

    /**
     * 사용자 맞춤 지표 선택으로 점수 계산
     */
    @Transactional(readOnly = true)
    public CustomScoreResponse calculateCustomScore(List<SettlementIndicator> indicators) {
        if (indicators == null || indicators.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 지표를 선택해야 합니다");
        }

        // 모든 자치구에 대해 맞춤 점수 계산
        List<CustomScoreResponse.DistrictCustomScore> districtScores = Arrays.stream(SeoulDistrict.values())
                .map(district -> calculateDistrictCustomScore(district, indicators))
                .sorted(Comparator.comparing(CustomScoreResponse.DistrictCustomScore::getTotalScore).reversed())
                .collect(Collectors.toList());

        // 서울시 평균 점수 계산
        double averageScore = districtScores.stream()
                .mapToDouble(CustomScoreResponse.DistrictCustomScore::getTotalScore)
                .average()
                .orElse(0.0);

        // 선택된 지표 정보
        List<CustomScoreResponse.IndicatorInfo> indicatorInfos = indicators.stream()
                .map(indicator -> CustomScoreResponse.IndicatorInfo.builder()
                        .indicator(indicator)
                        .displayName(indicator.getDisplayName())
                        .category(indicator.getCategory().getDisplayName())
                        .build())
                .collect(Collectors.toList());

        return CustomScoreResponse.builder()
                .districts(districtScores)
                .selectedIndicators(indicatorInfos)
                .averageScore(Math.round(averageScore * 10) / 10.0)
                .build();
    }

    /**
     * 특정 자치구의 맞춤 점수 계산
     */
    private CustomScoreResponse.DistrictCustomScore calculateDistrictCustomScore(
            SeoulDistrict district,
            List<SettlementIndicator> indicators
    ) {
        // 원시 데이터 조회
        DistrictRawData rawData = rawDataRepository.findByDistrict(district)
                .orElseThrow(() -> new IllegalStateException(
                        district.getDisplayName() + "의 원시 데이터가 없습니다. CSV 임포트를 먼저 실행해주세요."));

        // 각 지표별 점수 계산
        Map<SettlementIndicator, Double> indicatorScores = new LinkedHashMap<>();
        for (SettlementIndicator indicator : indicators) {
            double score = customScoreCalculator.calculateScore(List.of(indicator), rawData.getRawData());
            indicatorScores.put(indicator, score);
        }

        // 전체 점수는 모든 지표의 동일 가중치 평균
        double totalScore = customScoreCalculator.calculateScore(indicators, rawData.getRawData());

        return CustomScoreResponse.DistrictCustomScore.builder()
                .districtCode(district)
                .districtName(district.getDisplayName())
                .latitude(district.getLatitude())
                .longitude(district.getLongitude())
                .totalScore(totalScore)
                .indicatorScores(indicatorScores)
                .build();
    }
}
