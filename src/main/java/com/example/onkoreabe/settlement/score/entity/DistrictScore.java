package com.example.onkoreabe.settlement.score.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 자치구별 카테고리 점수 엔티티
 */
@Entity
@Table(
    name = "district_scores",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"district", "category"})
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeoulDistrict district;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoreCategory category;

    @Column(nullable = false)
    private Double score;  // 0.0 ~ 100.0
}
