package com.example.onkoreabe.settlement;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * 자치구별 원시 데이터 저장
 * 사용자 맞춤 지표 계산을 위해 CSV의 모든 원시 값을 저장
 */
@Entity
@Table(name = "district_raw_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DistrictRawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private SeoulDistrict district;

    /**
     * CSV의 모든 컬럼 데이터를 JSON 형태로 저장
     * Key: CSV 컬럼명, Value: 값
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private Map<String, String> rawData;

    public static DistrictRawData of(SeoulDistrict district, Map<String, String> rawData) {
        return DistrictRawData.builder()
                .district(district)
                .rawData(rawData)
                .build();
    }
}
