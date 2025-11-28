package com.example.onkoreabe.settlement;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV 파일에서 원시 데이터를 임포트하는 서비스
 * 애플리케이션 시작 시 DB에 데이터가 없으면 자동으로 임포트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataImportService {

    private final DistrictRawDataRepository rawDataRepository;

    /**
     * 애플리케이션 시작 시 자동 실행
     * DB에 데이터가 없으면 resources의 CSV 파일에서 자동 임포트
     */
    @PostConstruct
    public void autoImportIfNeeded() {
        try {
            long count = rawDataRepository.count();

            if (count == 0) {
                log.info("==============================================");
                log.info("원시 데이터 없음 - CSV 자동 임포트 시작");
                log.info("==============================================");

                importFromResources();

                log.info("==============================================");
                log.info("CSV 자동 임포트 완료 - {} 개 자치구 데이터 저장됨", rawDataRepository.count());
                log.info("==============================================");
            } else {
                log.info("원시 데이터 이미 존재 ({} 개 자치구) - 임포트 스킵", count);
            }
        } catch (Exception e) {
            log.error("CSV 자동 임포트 실패 - 서버는 정상 시작되지만 맞춤 점수 계산 불가", e);
        }
    }

    /**
     * resources/data 폴더의 CSV 파일들을 읽어서 임포트
     */
    private void importFromResources() throws IOException {
        InputStream educationCsv = new ClassPathResource("data/교육.csv").getInputStream();
        InputStream convenienceCsv = new ClassPathResource("data/생활편의교통.csv").getInputStream();
        InputStream safetyCsv = new ClassPathResource("data/안전.csv").getInputStream();
        InputStream populationCsv = new ClassPathResource("data/인구.csv").getInputStream();
        InputStream housingCsv = new ClassPathResource("data/주택.csv").getInputStream();
        InputStream welfareCsv = new ClassPathResource("data/복지문화.csv").getInputStream();

        importRawDataFromCsv(
                educationCsv,
                convenienceCsv,
                safetyCsv,
                populationCsv,
                housingCsv,
                welfareCsv
        );
    }

    /**
     * CSV 파일들을 읽어서 자치구별 원시 데이터 저장
     */
    @Transactional
    public void importRawDataFromCsv(
            InputStream educationCsv,
            InputStream convenienceCsv,
            InputStream safetyCsv,
            InputStream populationCsv,
            InputStream housingCsv,
            InputStream welfareCsv
    ) throws IOException {
        // 각 CSV를 파싱하여 자치구별 데이터 통합
        Map<String, Map<String, String>> educationData = parseCsv(educationCsv);
        Map<String, Map<String, String>> convenienceData = parseCsv(convenienceCsv);
        Map<String, Map<String, String>> safetyData = parseCsv(safetyCsv);
        Map<String, Map<String, String>> populationData = parseCsv(populationCsv);
        Map<String, Map<String, String>> housingData = parseCsv(housingCsv);
        Map<String, Map<String, String>> welfareData = parseCsv(welfareCsv);

        // 모든 자치구에 대해 데이터 통합 및 저장
        for (SeoulDistrict district : SeoulDistrict.values()) {
            String districtName = district.getDisplayName();

            Map<String, String> combinedData = new HashMap<>();

            // 각 카테고리 데이터 병합
            if (educationData.containsKey(districtName)) {
                combinedData.putAll(educationData.get(districtName));
            }
            if (convenienceData.containsKey(districtName)) {
                combinedData.putAll(convenienceData.get(districtName));
            }
            if (safetyData.containsKey(districtName)) {
                combinedData.putAll(safetyData.get(districtName));
            }
            if (populationData.containsKey(districtName)) {
                combinedData.putAll(populationData.get(districtName));
            }
            if (housingData.containsKey(districtName)) {
                combinedData.putAll(housingData.get(districtName));
            }
            if (welfareData.containsKey(districtName)) {
                combinedData.putAll(welfareData.get(districtName));
            }

            // 기존 데이터가 있으면 업데이트, 없으면 생성
            rawDataRepository.findByDistrict(district)
                    .ifPresentOrElse(
                            existing -> {
                                existing.getRawData().putAll(combinedData);
                                rawDataRepository.save(existing);
                            },
                            () -> rawDataRepository.save(DistrictRawData.of(district, combinedData))
                    );

            log.info("원시 데이터 저장 완료: {} ({} 개 지표)", districtName, combinedData.size());
        }
    }

    /**
     * CSV 파일 파싱
     * @return Map<자치구명, Map<컬럼명, 값>>
     */
    private Map<String, Map<String, String>> parseCsv(InputStream csvStream) throws IOException {
        Map<String, Map<String, String>> result = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {

            // UTF-8 BOM 제거
            reader.mark(1);
            if (reader.read() != 0xFEFF) {
                reader.reset();
            }

            // 헤더 읽기
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return result;
            }

            String[] headers = headerLine.split(",");

            // 데이터 읽기
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split(",", -1);  // -1: 빈 값도 포함

                if (values.length == 0) {
                    continue;
                }

                // 첫 번째 컬럼은 자치구명
                String districtName = values[0].trim()
                        .replace("　", "")  // 전각 공백 제거
                        .replace(" ", "");   // 일반 공백 제거

                Map<String, String> rowData = new HashMap<>();

                // 나머지 컬럼들을 Map에 저장
                for (int i = 1; i < Math.min(headers.length, values.length); i++) {
                    String columnName = headers[i].trim();
                    String value = values[i].trim();
                    rowData.put(columnName, value);
                }

                result.put(districtName, rowData);
            }
        }

        return result;
    }
}
