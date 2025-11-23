package com.example.onkoreabe.settlement;

import com.example.onkoreabe.settlement.dto.DistrictScoreResponse;
import com.example.onkoreabe.settlement.dto.SettlementMapResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * SettlementService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SettlementService 테스트")
class SettlementServiceTest {

    @Mock
    private DistrictScoreRepository districtScoreRepository;

    @InjectMocks
    private SettlementService settlementService;

    private List<DistrictScore> districtScores;

    @BeforeEach
    void setUp() {
        districtScores = List.of(
                DistrictScore.builder()
                        .id(1L)
                        .district(SeoulDistrict.GANGNAM)
                        .category(ScoreCategory.HOUSING)
                        .score(85.5)
                        .description("주거 환경이 매우 우수합니다")
                        .build(),
                DistrictScore.builder()
                        .id(2L)
                        .district(SeoulDistrict.GANGNAM)
                        .category(ScoreCategory.EDUCATION)
                        .score(92.5)
                        .description("교육 인프라가 매우 우수합니다")
                        .build(),
                DistrictScore.builder()
                        .id(3L)
                        .district(SeoulDistrict.SONGPA)
                        .category(ScoreCategory.HOUSING)
                        .score(83.0)
                        .description("신규 아파트 단지가 많습니다")
                        .build()
        );
    }

    @Test
    @DisplayName("전체 자치구 점수 조회 성공")
    void getAllDistrictScores_Success() {
        // given
        given(districtScoreRepository.findAll()).willReturn(districtScores);

        // when
        SettlementMapResponse response = settlementService.getAllDistrictScores();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTotalDistricts()).isEqualTo(25); // 서울시 25개 자치구
        assertThat(response.getDistricts()).isNotEmpty();
        assertThat(response.getAverageScore()).isGreaterThanOrEqualTo(0);

        verify(districtScoreRepository).findAll();
    }

    @Test
    @DisplayName("카테고리별 자치구 점수 조회 성공")
    void getDistrictScoresByCategory_Success() {
        // given
        List<DistrictScore> housingScores = districtScores.stream()
                .filter(score -> score.getCategory() == ScoreCategory.HOUSING)
                .toList();

        given(districtScoreRepository.findByCategory(ScoreCategory.HOUSING))
                .willReturn(housingScores);

        // when
        SettlementMapResponse response = settlementService
                .getDistrictScoresByCategory(ScoreCategory.HOUSING);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTotalDistricts()).isEqualTo(25);
        assertThat(response.getDistricts()).isNotEmpty();

        verify(districtScoreRepository).findByCategory(ScoreCategory.HOUSING);
    }

    @Test
    @DisplayName("특정 자치구 상세 점수 조회 성공")
    void getDistrictDetail_Success() {
        // given
        List<DistrictScore> gangnamScores = districtScores.stream()
                .filter(score -> score.getDistrict() == SeoulDistrict.GANGNAM)
                .toList();

        given(districtScoreRepository.findByDistrict(SeoulDistrict.GANGNAM))
                .willReturn(gangnamScores);

        // when
        DistrictScoreResponse response = settlementService
                .getDistrictDetail(SeoulDistrict.GANGNAM);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDistrictCode()).isEqualTo(SeoulDistrict.GANGNAM);
        assertThat(response.getDistrictName()).isEqualTo("강남구");
        assertThat(response.getCategoryScores()).hasSize(2);
        assertThat(response.getTotalScore()).isGreaterThan(0);

        verify(districtScoreRepository).findByDistrict(SeoulDistrict.GANGNAM);
    }
}
