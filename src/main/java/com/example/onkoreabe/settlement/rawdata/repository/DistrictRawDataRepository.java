package com.example.onkoreabe.settlement.rawdata.repository;

import com.example.onkoreabe.settlement.rawdata.entity.DistrictRawData;
import com.example.onkoreabe.settlement.score.entity.SeoulDistrict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 자치구 원시 데이터 Repository
 */
public interface DistrictRawDataRepository extends JpaRepository<DistrictRawData, Long> {

    /**
     * 특정 자치구의 원시 데이터 조회
     */
    Optional<DistrictRawData> findByDistrict(SeoulDistrict district);
}
