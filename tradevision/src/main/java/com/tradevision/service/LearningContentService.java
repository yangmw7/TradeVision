package com.tradevision.service;

import com.tradevision.constant.ContentType;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.dto.*;
import com.tradevision.entity.ContentModule;
import com.tradevision.entity.LearningContent;
import com.tradevision.entity.UserContentProgress;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.repository.ContentModuleRepository;
import com.tradevision.repository.LearningContentRepository;
import com.tradevision.repository.UserContentProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 학습 콘텐츠 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningContentService {

    private final ContentModuleRepository moduleRepository;
    private final LearningContentRepository contentRepository;
    private final UserContentProgressRepository progressRepository;

    /**
     * 모든 활성 모듈 조회
     *
     * @param difficulty 난이도 필터 (선택)
     * @param category   카테고리 필터 (선택)
     * @param pageable   페이징 정보
     * @param userId     사용자 ID (선택)
     * @return 모듈 목록
     */
    public ModuleListResponse getAllModules(InvestmentLevel difficulty, TechniqueCategory category,
                                            Pageable pageable, Long userId) {
        log.info("모듈 목록 조회 - 난이도: {}, 카테고리: {}", difficulty, category);

        Page<ContentModule> modulePage;

        if (difficulty != null && category != null) {
            modulePage = moduleRepository.findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByDisplayOrderAsc(
                    difficulty, category, pageable);
        } else if (difficulty != null) {
            modulePage = moduleRepository.findByDifficultyLevelAndIsActiveTrueOrderByDisplayOrderAsc(
                    difficulty, pageable);
        } else if (category != null) {
            modulePage = moduleRepository.findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(
                    category, pageable);
        } else {
            modulePage = moduleRepository.findByIsActiveTrueOrderByDisplayOrderAsc(pageable);
        }

        return buildModuleListResponse(modulePage, userId);
    }

    /**
     * 모듈 상세 조회
     *
     * @param moduleId 모듈 ID
     * @param userId   사용자 ID (선택)
     * @return 모듈 상세
     */
    public ModuleResponse getModuleById(Long moduleId, Long userId) {
        log.info("모듈 상세 조회 - 모듈 ID: {}", moduleId);

        ContentModule module = moduleRepository.findByIdAndIsActiveTrue(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LEARNING_CONTENT_NOT_FOUND));

        return buildModuleResponse(module, userId);
    }

    /**
     * 필수 모듈 조회
     *
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 필수 모듈 목록
     */
    public ModuleListResponse getRequiredModules(Pageable pageable, Long userId) {
        log.info("필수 모듈 조회");

        Page<ContentModule> modulePage = moduleRepository
                .findByIsRequiredTrueAndIsActiveTrueOrderByDisplayOrderAsc(pageable);

        return buildModuleListResponse(modulePage, userId);
    }

    /**
     * 모듈별 콘텐츠 조회
     *
     * @param moduleId 모듈 ID
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 콘텐츠 목록
     */
    public ContentListResponse getContentsByModule(Long moduleId, Pageable pageable, Long userId) {
        log.info("모듈별 콘텐츠 조회 - 모듈 ID: {}", moduleId);

        // 모듈 존재 확인
        moduleRepository.findByIdAndIsActiveTrue(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LEARNING_CONTENT_NOT_FOUND));

        Page<LearningContent> contentPage = contentRepository.findByModuleIdAndIsActiveTrue(moduleId, pageable);

        return buildContentListResponse(contentPage, userId);
    }

    /**
     * 콘텐츠 상세 조회
     *
     * @param contentId 콘텐츠 ID
     * @param userId    사용자 ID (선택)
     * @return 콘텐츠 상세
     */
    @Transactional
    public ContentResponse getContentById(Long contentId, Long userId) {
        log.info("콘텐츠 상세 조회 - 콘텐츠 ID: {}", contentId);

        LearningContent content = contentRepository.findByIdAndIsActiveTrue(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LEARNING_CONTENT_NOT_FOUND));

        // 조회수 증가
        contentRepository.incrementViewCount(contentId);

        return buildContentResponse(content, userId);
    }

    /**
     * 콘텐츠 타입별 조회
     *
     * @param contentType 콘텐츠 타입
     * @param pageable    페이징 정보
     * @param userId      사용자 ID (선택)
     * @return 콘텐츠 목록
     */
    public ContentListResponse getContentsByType(ContentType contentType, Pageable pageable, Long userId) {
        log.info("타입별 콘텐츠 조회 - 타입: {}", contentType);

        Page<LearningContent> contentPage = contentRepository
                .findByContentTypeAndIsActiveTrueOrderByCreatedAtDesc(contentType, pageable);

        return buildContentListResponse(contentPage, userId);
    }

    /**
     * 무료 콘텐츠 조회
     *
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 콘텐츠 목록
     */
    public ContentListResponse getFreeContents(Pageable pageable, Long userId) {
        log.info("무료 콘텐츠 조회");

        Page<LearningContent> contentPage = contentRepository
                .findByIsFreeTrueAndIsActiveTrueOrderByViewCountDesc(pageable);

        return buildContentListResponse(contentPage, userId);
    }

    /**
     * 인기 콘텐츠 조회
     *
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 콘텐츠 목록
     */
    public List<ContentResponse> getPopularContents(Pageable pageable, Long userId) {
        log.info("인기 콘텐츠 조회 - Top {}", pageable.getPageSize());

        List<LearningContent> contents = contentRepository.findTopByViewCount(pageable);

        return contents.stream()
                .map(content -> buildContentResponse(content, userId))
                .toList();
    }

    /**
     * 콘텐츠 검색
     *
     * @param keyword  검색어
     * @param pageable 페이징 정보
     * @param userId   사용자 ID (선택)
     * @return 검색 결과
     */
    public ContentListResponse searchContents(String keyword, Pageable pageable, Long userId) {
        log.info("콘텐츠 검색 - 키워드: {}", keyword);

        Page<LearningContent> contentPage = contentRepository.searchByKeyword(keyword, pageable);

        return buildContentListResponse(contentPage, userId);
    }

    /**
     * ModuleListResponse 빌더
     */
    private ModuleListResponse buildModuleListResponse(Page<ContentModule> modulePage, Long userId) {
        return ModuleListResponse.builder()
                .modules(modulePage.getContent().stream()
                        .map(module -> buildModuleResponse(module, userId))
                        .toList())
                .currentPage(modulePage.getNumber())
                .totalPages(modulePage.getTotalPages())
                .totalElements(modulePage.getTotalElements())
                .pageSize(modulePage.getSize())
                .last(modulePage.isLast())
                .build();
    }

    /**
     * ModuleResponse 빌더
     */
    private ModuleResponse buildModuleResponse(ContentModule module, Long userId) {
        long contentCount = contentRepository.countByModuleIdAndIsActiveTrue(module.getId());

        ModuleResponse.ModuleResponseBuilder builder = ModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .titleEn(module.getTitleEn())
                .description(module.getDescription())
                .difficultyLevel(module.getDifficultyLevel())
                .category(module.getCategory())
                .displayOrder(module.getDisplayOrder())
                .estimatedDurationMinutes(module.getEstimatedDurationMinutes())
                .thumbnailUrl(module.getThumbnailUrl())
                .isRequired(module.getIsRequired())
                .contentCount(contentCount)
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt());

        // 사용자 진행 정보 추가
        if (userId != null) {
            List<UserContentProgress> moduleProgress = progressRepository
                    .findByUserIdAndModuleId(userId, module.getId());

            long completedCount = moduleProgress.stream()
                    .filter(UserContentProgress::getIsCompleted)
                    .count();

            int progressPercentage = contentCount > 0
                    ? (int) ((completedCount * 100) / contentCount)
                    : 0;

            builder.userProgress(ModuleResponse.UserModuleProgress.builder()
                    .completedContentCount(completedCount)
                    .totalContentCount(contentCount)
                    .progressPercentage(progressPercentage)
                    .isCompleted(completedCount == contentCount && contentCount > 0)
                    .build());
        }

        return builder.build();
    }

    /**
     * ContentListResponse 빌더
     */
    private ContentListResponse buildContentListResponse(Page<LearningContent> contentPage, Long userId) {
        return ContentListResponse.builder()
                .contents(contentPage.getContent().stream()
                        .map(content -> buildContentResponse(content, userId))
                        .toList())
                .currentPage(contentPage.getNumber())
                .totalPages(contentPage.getTotalPages())
                .totalElements(contentPage.getTotalElements())
                .pageSize(contentPage.getSize())
                .last(contentPage.isLast())
                .build();
    }

    /**
     * ContentResponse 빌더
     */
    private ContentResponse buildContentResponse(LearningContent content, Long userId) {
        List<String> imageUrls = content.getImageUrls() != null
                ? Arrays.asList(content.getImageUrls().split(","))
                : List.of();

        ContentResponse.ContentResponseBuilder builder = ContentResponse.builder()
                .id(content.getId())
                .moduleId(content.getModule().getId())
                .moduleName(content.getModule().getTitle())
                .title(content.getTitle())
                .titleEn(content.getTitleEn())
                .summary(content.getSummary())
                .contentBody(content.getContentBody())
                .contentType(content.getContentType())
                .displayOrder(content.getDisplayOrder())
                .estimatedDurationMinutes(content.getEstimatedDurationMinutes())
                .videoUrl(content.getVideoUrl())
                .imageUrls(imageUrls)
                .isFree(content.getIsFree())
                .viewCount(content.getViewCount())
                .likeCount(content.getLikeCount())
                .quizData(content.getQuizData())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt());

        // 사용자 진행 정보 추가
        if (userId != null) {
            progressRepository.findByUserIdAndContentId(userId, content.getId())
                    .ifPresent(progress -> builder.userProgress(
                            ContentResponse.UserContentProgressInfo.builder()
                                    .isCompleted(progress.getIsCompleted())
                                    .progressPercentage(progress.getProgressPercentage())
                                    .isLiked(progress.getIsLiked())
                                    .isBookmarked(progress.getIsBookmarked())
                                    .totalTimeSpentSeconds(progress.getTotalTimeSpentSeconds())
                                    .quizScore(progress.getQuizScore())
                                    .lastAccessedAt(progress.getLastAccessedAt())
                                    .build()
                    ));
        }

        return builder.build();
    }
}
