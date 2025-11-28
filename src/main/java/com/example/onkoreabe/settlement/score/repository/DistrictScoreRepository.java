package com.example.onkoreabe.settlement.score.repository;

import com.example.onkoreabe.settlement.score.entity.DistrictScore;
import com.example.onkoreabe.settlement.score.entity.ScoreCategory;
import com.example.onkoreabe.settlement.score.entity.SeoulDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 자치구 점수 Repository
 */
@Repository
public interface DistrictScoreRepository extends JpaRepository<DistrictScore, Long> {

    /**
     * 특정 자치구의 모든 카테고리 점수 조회
     */
    List<DistrictScore> findByDistrict(SeoulDistrict district);

    /**
     * 특정 카테고리의 모든 자치구 점수 조회
     */
    List<DistrictScore> findByCategory(ScoreCategory category);

    /**
     * 자치구 목록의 모든 점수 조회
     */
    List<DistrictScore> findByDistrictIn(List<SeoulDistrict> districts);
}
