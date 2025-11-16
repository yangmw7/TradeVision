package com.tradevision.service;

import com.tradevision.dto.ContentProgressRequest;
import com.tradevision.dto.ContentProgressResponse;
import com.tradevision.dto.UserLearningStatsResponse;
import com.tradevision.entity.LearningContent;
import com.tradevision.entity.User;
import com.tradevision.entity.UserContentProgress;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.repository.LearningContentRepository;
import com.tradevision.repository.UserContentProgressRepository;
import com.tradevision.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 콘텐츠 진행도 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentProgressService {

    private final UserContentProgressRepository progressRepository;
    private final LearningContentRepository contentRepository;
    private final UserRepository userRepository;

    /**
     * 진행도 업데이트
     *
     * @param contentId 콘텐츠 ID
     * @param userId    사용자 ID
     * @param request   진행도 요청
     * @return 진행도 응답
     */
    @Transactional
    public ContentProgressResponse updateProgress(Long contentId, Long userId, ContentProgressRequest request) {
        log.info("진행도 업데이트 - 사용자: {}, 콘텐츠: {}, 진행률: {}%",
                userId, contentId, request.getProgressPercentage());

        // 콘텐츠 확인
        LearningContent content = contentRepository.findByIdAndIsActiveTrue(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LEARNING_CONTENT_NOT_FOUND));

        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 진행도 조회 또는 생성
        UserContentProgress progress = progressRepository
                .findByUserIdAndContentId(userId, contentId)
                .orElseGet(() -> UserContentProgress.builder()
                        .user(user)
                        .content(content)
                        .build());

        // 진행도 업데이트
        progress.updateProgress(request.getProgressPercentage());

        // 학습 시간 추가
        if (request.getTimeSpentSeconds() != null && request.getTimeSpentSeconds() > 0) {
            progress.addTimeSpent(request.getTimeSpentSeconds());
        }

        // 퀴즈 점수 업데이트
        if (request.getQuizScore() != null) {
            progress.updateQuizScore(request.getQuizScore(), request.getQuizAnswers());
        }

        // 노트 업데이트
        if (request.getUserNotes() != null) {
            progress.updateNotes(request.getUserNotes());
        }

        // 저장
        UserContentProgress savedProgress = progressRepository.save(progress);

        return buildProgressResponse(savedProgress);
    }

    /**
     * 콘텐츠 좋아요 토글
     *
     * @param contentId 콘텐츠 ID
     * @param userId    사용자 ID
     * @return 진행도 응답
     */
    @Transactional
    public ContentProgressResponse toggleLike(Long contentId, Long userId) {
        log.info("좋아요 토글 - 사용자: {}, 콘텐츠: {}", userId, contentId);

        UserContentProgress progress = getOrCreateProgress(contentId, userId);

        boolean wasLiked = progress.getIsLiked();
        progress.toggleLike();

        // 콘텐츠의 좋아요 수 업데이트
        if (progress.getIsLiked()) {
            contentRepository.incrementLikeCount(contentId);
        } else {
            contentRepository.decrementLikeCount(contentId);
        }

        UserContentProgress savedProgress = progressRepository.save(progress);

        log.info("좋아요 변경: {} -> {}", wasLiked, progress.getIsLiked());

        return buildProgressResponse(savedProgress);
    }

    /**
     * 콘텐츠 북마크 토글
     *
     * @param contentId 콘텐츠 ID
     * @param userId    사용자 ID
     * @return 진행도 응답
     */
    @Transactional
    public ContentProgressResponse toggleBookmark(Long contentId, Long userId) {
        log.info("북마크 토글 - 사용자: {}, 콘텐츠: {}", userId, contentId);

        UserContentProgress progress = getOrCreateProgress(contentId, userId);
        progress.toggleBookmark();

        UserContentProgress savedProgress = progressRepository.save(progress);

        return buildProgressResponse(savedProgress);
    }

    /**
     * 콘텐츠 완료 처리
     *
     * @param contentId 콘텐츠 ID
     * @param userId    사용자 ID
     * @return 진행도 응답
     */
    @Transactional
    public ContentProgressResponse markAsCompleted(Long contentId, Long userId) {
        log.info("콘텐츠 완료 처리 - 사용자: {}, 콘텐츠: {}", userId, contentId);

        UserContentProgress progress = getOrCreateProgress(contentId, userId);
        progress.markAsCompleted();

        UserContentProgress savedProgress = progressRepository.save(progress);

        return buildProgressResponse(savedProgress);
    }

    /**
     * 사용자의 진행도 목록 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    public Page<ContentProgressResponse> getUserProgress(Long userId, Pageable pageable) {
        log.info("사용자 진행도 목록 조회 - 사용자: {}", userId);

        Page<UserContentProgress> progressPage = progressRepository
                .findByUserIdOrderByLastAccessedAtDesc(userId, pageable);

        return progressPage.map(this::buildProgressResponse);
    }

    /**
     * 사용자의 완료된 콘텐츠 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    public Page<ContentProgressResponse> getCompletedContents(Long userId, Pageable pageable) {
        log.info("완료된 콘텐츠 조회 - 사용자: {}", userId);

        Page<UserContentProgress> progressPage = progressRepository
                .findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(userId, pageable);

        return progressPage.map(this::buildProgressResponse);
    }

    /**
     * 사용자의 진행중인 콘텐츠 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    public Page<ContentProgressResponse> getInProgressContents(Long userId, Pageable pageable) {
        log.info("진행중인 콘텐츠 조회 - 사용자: {}", userId);

        Page<UserContentProgress> progressPage = progressRepository
                .findInProgressContents(userId, pageable);

        return progressPage.map(this::buildProgressResponse);
    }

    /**
     * 사용자의 북마크된 콘텐츠 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    public Page<ContentProgressResponse> getBookmarkedContents(Long userId, Pageable pageable) {
        log.info("북마크된 콘텐츠 조회 - 사용자: {}", userId);

        Page<UserContentProgress> progressPage = progressRepository
                .findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(userId, pageable);

        return progressPage.map(this::buildProgressResponse);
    }

    /**
     * 사용자의 학습 통계 조회
     *
     * @param userId 사용자 ID
     * @return 학습 통계
     */
    public UserLearningStatsResponse getUserLearningStats(Long userId) {
        log.info("사용자 학습 통계 조회 - 사용자: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 완료한 콘텐츠 수
        long completedCount = progressRepository.countByUserIdAndIsCompletedTrue(userId);

        // 진행중인 콘텐츠 수
        long inProgressCount = progressRepository.findInProgressContents(userId, Pageable.unpaged())
                .getTotalElements();

        // 북마크한 콘텐츠 수
        long bookmarkedCount = progressRepository
                .findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(userId, Pageable.unpaged())
                .getTotalElements();

        // 총 학습 시간
        Long totalSeconds = progressRepository.calculateTotalTimeSpent(userId);
        if (totalSeconds == null) {
            totalSeconds = 0L;
        }

        // 평균 진행률
        Double averageProgress = progressRepository.calculateAverageProgress(userId);
        if (averageProgress == null) {
            averageProgress = 0.0;
        }

        // 평균 퀴즈 점수
        Double averageQuizScore = progressRepository.calculateAverageQuizScore(userId);

        // 학습 레벨 계산 (완료 개수 기반)
        String learningLevel = calculateLearningLevel(completedCount);
        int contentsToNextLevel = calculateContentsToNextLevel(completedCount);

        return UserLearningStatsResponse.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .completedContentCount(completedCount)
                .inProgressContentCount(inProgressCount)
                .bookmarkedContentCount(bookmarkedCount)
                .totalTimeSpentSeconds(totalSeconds)
                .totalTimeSpentMinutes(totalSeconds / 60)
                .totalTimeSpentHours(totalSeconds / 3600)
                .averageProgress(Math.round(averageProgress * 10.0) / 10.0)
                .averageQuizScore(averageQuizScore != null ? Math.round(averageQuizScore * 10.0) / 10.0 : null)
                .studyStreak(0) // TODO: 실제 스트릭 계산 로직 구현
                .learningLevel(learningLevel)
                .contentsToNextLevel(contentsToNextLevel)
                .build();
    }

    /**
     * 진행도 조회 또는 생성
     */
    private UserContentProgress getOrCreateProgress(Long contentId, Long userId) {
        LearningContent content = contentRepository.findByIdAndIsActiveTrue(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LEARNING_CONTENT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return progressRepository.findByUserIdAndContentId(userId, contentId)
                .orElseGet(() -> UserContentProgress.builder()
                        .user(user)
                        .content(content)
                        .build());
    }

    /**
     * 학습 레벨 계산
     */
    private String calculateLearningLevel(long completedCount) {
        if (completedCount >= 50) return "마스터";
        if (completedCount >= 30) return "전문가";
        if (completedCount >= 15) return "숙련자";
        if (completedCount >= 5) return "학습자";
        return "초보자";
    }

    /**
     * 다음 레벨까지 필요한 콘텐츠 수 계산
     */
    private int calculateContentsToNextLevel(long completedCount) {
        if (completedCount >= 50) return 0; // 최고 레벨
        if (completedCount >= 30) return (int) (50 - completedCount);
        if (completedCount >= 15) return (int) (30 - completedCount);
        if (completedCount >= 5) return (int) (15 - completedCount);
        return (int) (5 - completedCount);
    }

    /**
     * ContentProgressResponse 빌더
     */
    private ContentProgressResponse buildProgressResponse(UserContentProgress progress) {
        return ContentProgressResponse.builder()
                .id(progress.getId())
                .userId(progress.getUser().getId())
                .contentId(progress.getContent().getId())
                .contentTitle(progress.getContent().getTitle())
                .moduleId(progress.getContent().getModule().getId())
                .moduleName(progress.getContent().getModule().getTitle())
                .isCompleted(progress.getIsCompleted())
                .progressPercentage(progress.getProgressPercentage())
                .isLiked(progress.getIsLiked())
                .isBookmarked(progress.getIsBookmarked())
                .totalTimeSpentSeconds(progress.getTotalTimeSpentSeconds())
                .quizScore(progress.getQuizScore())
                .quizAnswers(progress.getQuizAnswers())
                .userNotes(progress.getUserNotes())
                .lastAccessedAt(progress.getLastAccessedAt())
                .completedAt(progress.getCompletedAt())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
