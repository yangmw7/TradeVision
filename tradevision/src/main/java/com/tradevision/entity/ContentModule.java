package com.tradevision.entity;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학습 콘텐츠 모듈 엔티티
 * 관련 학습 콘텐츠를 그룹화하는 모듈/챕터
 */
@Entity
@Table(name = "content_modules")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentModule extends BaseTimeEntity {

    /**
     * 모듈 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 모듈명 (한글)
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 모듈명 (영문)
     */
    @Column(name = "title_en", length = 200)
    private String titleEn;

    /**
     * 모듈 설명
     */
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 난이도
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private InvestmentLevel difficultyLevel;

    /**
     * 카테고리 (기법 카테고리와 연결)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TechniqueCategory category;

    /**
     * 모듈 순서 (정렬용)
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    /**
     * 예상 학습 시간 (분)
     */
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    /**
     * 활성화 여부
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 썸네일 이미지 URL
     */
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    /**
     * 필수 모듈 여부
     */
    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = false;
}
