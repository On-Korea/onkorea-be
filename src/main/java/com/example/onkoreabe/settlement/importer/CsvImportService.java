package com.example.onkoreabe.settlement.importer;

import com.example.onkoreabe.settlement.DistrictScore;
import com.example.onkoreabe.settlement.DistrictScoreRepository;
import com.example.onkoreabe.settlement.ScoreCategory;
import com.example.onkoreabe.settlement.SeoulDistrict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CSV 파일 임포트 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CsvImportService {

    private final DistrictScoreRepository districtScoreRepository;
    private final ScoreCalculatorFactory scoreCalculatorFactory;

    /**
     * CSV 파일을 임포트하여 점수 계산 및 저장
     *
     * @param file     업로드된 CSV 파일
     * @param category 카테고리
     * @return 저장된 레코드 수
     */
    @Transactional
    public int importCsvFile(MultipartFile file, ScoreCategory category) throws Exception {
        log.info("CSV 파일 임포트 시작: category={}, filename={}", category, file.getOriginalFilename());

        List<DistrictScore> scores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            ScoreCalculator calculator = scoreCalculatorFactory.getCalculator(category);

            for (CSVRecord record : csvParser) {
                try {
                    // 자치구명 추출
                    String districtName = record.get("district");
                    SeoulDistrict district = findDistrictByName(districtName);

                    if (district == null) {
                        log.warn("알 수 없는 자치구명: {}", districtName);
                        continue;
                    }

                    // CSV 레코드를 Map으로 변환
                    Map<String, String> data = record.toMap();

                    // 점수 계산
                    double score = calculator.calculateScore(data);
                    String description = calculator.generateDescription(data);

                    // DistrictScore 생성
                    DistrictScore districtScore = DistrictScore.builder()
                            .district(district)
                            .category(category)
                            .score(score)
                            .description(description)
                            .build();

                    scores.add(districtScore);

                } catch (Exception e) {
                    log.error("CSV 레코드 처리 중 오류: {}", record, e);
                }
            }

            // 기존 데이터 삭제 후 새로 저장
            districtScoreRepository.deleteAll(
                    districtScoreRepository.findByCategory(category)
            );
            districtScoreRepository.saveAll(scores);

            log.info("CSV 임포트 완료: {} 건 저장됨", scores.size());
            return scores.size();

        } catch (Exception e) {
            log.error("CSV 파일 임포트 실패", e);
            throw new RuntimeException("CSV 파일 임포트 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 자치구명으로 SeoulDistrict enum 찾기
     */
    private SeoulDistrict findDistrictByName(String name) {
        for (SeoulDistrict district : SeoulDistrict.values()) {
            if (district.getDisplayName().equals(name)) {
                return district;
            }
        }
        return null;
    }
}
