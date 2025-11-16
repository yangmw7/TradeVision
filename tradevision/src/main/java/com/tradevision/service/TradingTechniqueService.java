package com.tradevision.service;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.ProgressStatus;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.dto.*;
import com.tradevision.entity.TradingTechnique;
import com.tradevision.entity.User;
import com.tradevision.entity.UserTechniqueProgress;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.repository.TradingTechniqueRepository;
import com.tradevision.repository.UserRepository;
import com.tradevision.repository.UserTechniqueProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매매기법 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradingTechniqueService {

    private final TradingTechniqueRepository techniqueRepository;
    private final UserTechniqueProgressRepository progressRepository;
    private final UserRepository userRepository;

    /**
     * 모든 활성 기법 조회
     *
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택, 진행도 정보 포함)
     * @return 기법 목록
     */
    public TechniqueListResponse getAllTechniques(Pageable pageable, Long userId) {
        log.info("모든 기법 조회 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<TradingTechnique> techniquePage = techniqueRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);

        return buildTechniqueListResponse(techniquePage, userId);
    }

    /**
     * 기법 상세 조회
     *
     * @param techniqueId 기법 ID
     * @param userId      사용자 ID (선택, 진행도 정보 포함)
     * @return 기법 상세
     */
    @Transactional
    public TechniqueResponse getTechniqueById(Long techniqueId, Long userId) {
        log.info("기법 상세 조회 - 기법 ID: {}, 사용자 ID: {}", techniqueId, userId);

        TradingTechnique technique = techniqueRepository.findByIdAndIsActiveTrue(techniqueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TECHNIQUE_NOT_FOUND));

        // 조회수 증가
        techniqueRepository.incrementViewCount(techniqueId);

        return buildTechniqueResponse(technique, userId);
    }

    /**
     * 난이도별 기법 조회
     *
     * @param level    난이도
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 기법 목록
     */
    public TechniqueListResponse getTechniquesByDifficulty(InvestmentLevel level, Pageable pageable, Long userId) {
        log.info("난이도별 기법 조회 - 난이도: {}", level);

        Page<TradingTechnique> techniquePage = techniqueRepository
                .findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(level, pageable);

        return buildTechniqueListResponse(techniquePage, userId);
    }

    /**
     * 카테고리별 기법 조회
     *
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 기법 목록
     */
    public TechniqueListResponse getTechniquesByCategory(TechniqueCategory category, Pageable pageable, Long userId) {
        log.info("카테고리별 기법 조회 - 카테고리: {}", category);

        Page<TradingTechnique> techniquePage = techniqueRepository
                .findByCategoryAndIsActiveTrueOrderByViewCountDesc(category, pageable);

        return buildTechniqueListResponse(techniquePage, userId);
    }

    /**
     * 난이도와 카테고리로 기법 조회
     *
     * @param level    난이도
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 기법 목록
     */
    public TechniqueListResponse getTechniquesByDifficultyAndCategory(
            InvestmentLevel level, TechniqueCategory category, Pageable pageable, Long userId) {
        log.info("난이도&카테고리별 기법 조회 - 난이도: {}, 카테고리: {}", level, category);

        Page<TradingTechnique> techniquePage = techniqueRepository
                .findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByRecommendationCountDesc(level, category, pageable);

        return buildTechniqueListResponse(techniquePage, userId);
    }

    /**
     * 키워드로 기법 검색
     *
     * @param keyword  검색어
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 기법 목록
     */
    public TechniqueListResponse searchTechniques(String keyword, Pageable pageable, Long userId) {
        log.info("기법 검색 - 키워드: {}", keyword);

        Page<TradingTechnique> techniquePage = techniqueRepository.searchByKeyword(keyword, pageable);

        return buildTechniqueListResponse(techniquePage, userId);
    }

    /**
     * 인기 기법 조회 (조회수 기준)
     *
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 기법 목록
     */
    public TechniqueListResponse getPopularTechniques(Pageable pageable, Long userId) {
        log.info("인기 기법 조회 - Top {}", pageable.getPageSize());

        Page<TradingTechnique> techniquePage = Page.empty();
        var techniques = techniqueRepository.findTopByViewCount(pageable);

        // List를 Page로 변환
        techniquePage = techniqueRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);

        return buildTechniqueListResponse(techniquePage, userId);
    }

    /**
     * 사용자 학습 진행도 업데이트
     *
     * @param techniqueId 기법 ID
     * @param userId      사용자 ID
     * @param request     진행도 요청
     * @return 진행도 응답
     */
    @Transactional
    public ProgressResponse updateProgress(Long techniqueId, Long userId, ProgressRequest request) {
        log.info("진행도 업데이트 - 사용자: {}, 기법: {}, 상태: {}, 진행률: {}%",
                userId, techniqueId, request.getStatus(), request.getProgressPercentage());

        // 기법 확인
        TradingTechnique technique = techniqueRepository.findByIdAndIsActiveTrue(techniqueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TECHNIQUE_NOT_FOUND));

        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 진행도 조회 또는 생성
        UserTechniqueProgress progress = progressRepository
                .findByUserIdAndTechniqueId(userId, techniqueId)
                .orElseGet(() -> UserTechniqueProgress.builder()
                        .user(user)
                        .technique(technique)
                        .build());

        // 진행도 업데이트
        progress.updateProgress(request.getStatus(), request.getProgressPercentage());

        // 북마크 업데이트 (요청에 있는 경우)
        if (request.getIsBookmarked() != null && !request.getIsBookmarked().equals(progress.getIsBookmarked())) {
            progress.toggleBookmark();
        }

        // 평점 업데이트 (요청에 있는 경우)
        if (request.getUserRating() != null) {
            progress.updateRating(request.getUserRating());
        }

        // 메모 업데이트 (요청에 있는 경우)
        if (request.getUserNotes() != null) {
            progress.updateNotes(request.getUserNotes());
        }

        // 저장
        UserTechniqueProgress savedProgress = progressRepository.save(progress);

        return buildProgressResponse(savedProgress);
    }

    /**
     * 사용자의 특정 기법 진행도 조회
     *
     * @param techniqueId 기법 ID
     * @param userId      사용자 ID
     * @return 진행도 응답
     */
    public ProgressResponse getUserProgress(Long techniqueId, Long userId) {
        log.info("사용자 진행도 조회 - 사용자: {}, 기법: {}", userId, techniqueId);

        UserTechniqueProgress progress = progressRepository
                .findByUserIdAndTechniqueId(userId, techniqueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROGRESS_NOT_FOUND));

        return buildProgressResponse(progress);
    }

    /**
     * 사용자의 전체 진행도 목록 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    public Page<ProgressResponse> getUserProgressHistory(Long userId, Pageable pageable) {
        log.info("사용자 진행도 목록 조회 - 사용자: {}", userId);

        Page<UserTechniqueProgress> progressPage = progressRepository
                .findByUserIdOrderByUpdatedAtDesc(userId, pageable);

        return progressPage.map(this::buildProgressResponse);
    }

    /**
     * 사용자의 상태별 진행도 조회
     *
     * @param userId   사용자 ID
     * @param status   상태
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    public Page<ProgressResponse> getUserProgressByStatus(Long userId, ProgressStatus status, Pageable pageable) {
        log.info("사용자 상태별 진행도 조회 - 사용자: {}, 상태: {}", userId, status);

        Page<UserTechniqueProgress> progressPage = progressRepository
                .findByUserIdAndStatusOrderByUpdatedAtDesc(userId, status, pageable);

        return progressPage.map(this::buildProgressResponse);
    }

    /**
     * TechniqueListResponse 빌더
     */
    private TechniqueListResponse buildTechniqueListResponse(Page<TradingTechnique> techniquePage, Long userId) {
        return TechniqueListResponse.builder()
                .techniques(techniquePage.getContent().stream()
                        .map(t -> buildTechniqueResponse(t, userId))
                        .toList())
                .currentPage(techniquePage.getNumber())
                .totalPages(techniquePage.getTotalPages())
                .totalElements(techniquePage.getTotalElements())
                .pageSize(techniquePage.getSize())
                .last(techniquePage.isLast())
                .build();
    }

    /**
     * TechniqueResponse 빌더
     */
    private TechniqueResponse buildTechniqueResponse(TradingTechnique technique, Long userId) {
        TechniqueResponse.TechniqueResponseBuilder builder = TechniqueResponse.builder()
                .id(technique.getId())
                .name(technique.getName())
                .nameEn(technique.getNameEn())
                .difficultyLevel(technique.getDifficultyLevel())
                .category(technique.getCategory())
                .summary(technique.getSummary())
                .description(technique.getDescription())
                .usageGuide(technique.getUsageGuide())
                .exampleScenario(technique.getExampleScenario())
                .advantages(technique.getAdvantages())
                .disadvantages(technique.getDisadvantages())
                .riskLevel(technique.getRiskLevel())
                .viewCount(technique.getViewCount())
                .recommendationCount(technique.getRecommendationCount())
                .createdAt(technique.getCreatedAt())
                .updatedAt(technique.getUpdatedAt());

        // 사용자 진행 정보 추가 (로그인한 경우)
        if (userId != null) {
            progressRepository.findByUserIdAndTechniqueId(userId, technique.getId())
                    .ifPresent(progress -> builder.userProgress(
                            TechniqueResponse.UserProgressInfo.builder()
                                    .status(progress.getStatus().name())
                                    .progressPercentage(progress.getProgressPercentage())
                                    .isBookmarked(progress.getIsBookmarked())
                                    .userRating(progress.getUserRating())
                                    .isCompleted(progress.getIsCompleted())
                                    .build()
                    ));
        }

        return builder.build();
    }

    /**
     * ProgressResponse 빌더
     */
    private ProgressResponse buildProgressResponse(UserTechniqueProgress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .userId(progress.getUser().getId())
                .techniqueId(progress.getTechnique().getId())
                .techniqueName(progress.getTechnique().getName())
                .status(progress.getStatus())
                .progressPercentage(progress.getProgressPercentage())
                .isBookmarked(progress.getIsBookmarked())
                .userRating(progress.getUserRating())
                .userNotes(progress.getUserNotes())
                .isCompleted(progress.getIsCompleted())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
