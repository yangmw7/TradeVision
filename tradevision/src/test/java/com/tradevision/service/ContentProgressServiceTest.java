package com.tradevision.service;

import com.tradevision.dto.ContentProgressRequest;
import com.tradevision.dto.ContentProgressResponse;
import com.tradevision.dto.UserLearningStatsResponse;
import com.tradevision.entity.ContentModule;
import com.tradevision.entity.LearningContent;
import com.tradevision.entity.User;
import com.tradevision.entity.UserContentProgress;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.repository.LearningContentRepository;
import com.tradevision.repository.UserContentProgressRepository;
import com.tradevision.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * ContentProgressService 테스트
 */
@ExtendWith(MockitoExtension.class)
class ContentProgressServiceTest {

    @Mock
    private UserContentProgressRepository progressRepository;

    @Mock
    private LearningContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContentProgressService contentProgressService;

    private User user;
    private ContentModule module;
    private LearningContent content;
    private UserContentProgress progress;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .build();

        module = ContentModule.builder()
                .id(1L)
                .title("이동평균선 학습")
                .build();

        content = LearningContent.builder()
                .id(1L)
                .module(module)
                .title("이동평균선이란?")
                .build();

        progress = UserContentProgress.builder()
                .id(1L)
                .user(user)
                .content(content)
                .progressPercentage(0)
                .totalTimeSpentSeconds(0L)
                .build();
    }

    @Test
    @DisplayName("진행도 업데이트 - 성공")
    void updateProgress_Success() {
        // given
        ContentProgressRequest request = ContentProgressRequest.builder()
                .progressPercentage(50)
                .timeSpentSeconds(300L)
                .build();

        given(contentRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(content));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.findByUserIdAndContentId(1L, 1L)).willReturn(Optional.of(progress));
        given(progressRepository.save(any(UserContentProgress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContentProgressResponse response = contentProgressService.updateProgress(1L, 1L, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getProgressPercentage()).isEqualTo(50);
        verify(progressRepository).save(any(UserContentProgress.class));
    }

    @Test
    @DisplayName("진행도 업데이트 - 퀴즈 점수 포함")
    void updateProgress_WithQuizScore() {
        // given
        ContentProgressRequest request = ContentProgressRequest.builder()
                .progressPercentage(100)
                .quizScore(85)
                .quizAnswers("{\"q1\":\"a\",\"q2\":\"b\"}")
                .build();

        given(contentRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(content));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.findByUserIdAndContentId(1L, 1L)).willReturn(Optional.of(progress));
        given(progressRepository.save(any(UserContentProgress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContentProgressResponse response = contentProgressService.updateProgress(1L, 1L, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getQuizScore()).isEqualTo(85);
        assertThat(response.getQuizAnswers()).isEqualTo("{\"q1\":\"a\",\"q2\":\"b\"}");
    }

    @Test
    @DisplayName("진행도 업데이트 - 콘텐츠 없음")
    void updateProgress_ContentNotFound() {
        // given
        ContentProgressRequest request = ContentProgressRequest.builder()
                .progressPercentage(50)
                .build();

        given(contentRepository.findByIdAndIsActiveTrue(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentProgressService.updateProgress(999L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LEARNING_CONTENT_NOT_FOUND);
    }

    @Test
    @DisplayName("좋아요 토글 - 성공")
    void toggleLike_Success() {
        // given
        given(contentRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(content));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.findByUserIdAndContentId(1L, 1L)).willReturn(Optional.of(progress));
        given(progressRepository.save(any(UserContentProgress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContentProgressResponse response = contentProgressService.toggleLike(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsLiked()).isTrue();
        verify(contentRepository).incrementLikeCount(1L);
    }

    @Test
    @DisplayName("북마크 토글 - 성공")
    void toggleBookmark_Success() {
        // given
        given(contentRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(content));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.findByUserIdAndContentId(1L, 1L)).willReturn(Optional.of(progress));
        given(progressRepository.save(any(UserContentProgress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContentProgressResponse response = contentProgressService.toggleBookmark(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsBookmarked()).isTrue();
    }

    @Test
    @DisplayName("콘텐츠 완료 처리 - 성공")
    void markAsCompleted_Success() {
        // given
        given(contentRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(content));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.findByUserIdAndContentId(1L, 1L)).willReturn(Optional.of(progress));
        given(progressRepository.save(any(UserContentProgress.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContentProgressResponse response = contentProgressService.markAsCompleted(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsCompleted()).isTrue();
        assertThat(response.getProgressPercentage()).isEqualTo(100);
    }

    @Test
    @DisplayName("사용자 진행도 목록 조회 - 성공")
    void getUserProgress_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserContentProgress> progressPage = new PageImpl<>(List.of(progress), pageable, 1);

        given(progressRepository.findByUserIdOrderByLastAccessedAtDesc(1L, pageable)).willReturn(progressPage);

        // when
        Page<ContentProgressResponse> response = contentProgressService.getUserProgress(1L, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("완료된 콘텐츠 조회 - 성공")
    void getCompletedContents_Success() {
        // given
        progress.markAsCompleted();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserContentProgress> progressPage = new PageImpl<>(List.of(progress), pageable, 1);

        given(progressRepository.findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(1L, pageable))
                .willReturn(progressPage);

        // when
        Page<ContentProgressResponse> response = contentProgressService.getCompletedContents(1L, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("진행중인 콘텐츠 조회 - 성공")
    void getInProgressContents_Success() {
        // given
        progress.updateProgress(50);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserContentProgress> progressPage = new PageImpl<>(List.of(progress), pageable, 1);

        given(progressRepository.findInProgressContents(1L, pageable)).willReturn(progressPage);

        // when
        Page<ContentProgressResponse> response = contentProgressService.getInProgressContents(1L, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("북마크된 콘텐츠 조회 - 성공")
    void getBookmarkedContents_Success() {
        // given
        progress.toggleBookmark();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserContentProgress> progressPage = new PageImpl<>(List.of(progress), pageable, 1);

        given(progressRepository.findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(1L, pageable))
                .willReturn(progressPage);

        // when
        Page<ContentProgressResponse> response = contentProgressService.getBookmarkedContents(1L, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("사용자 학습 통계 조회 - 성공")
    void getUserLearningStats_Success() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.countByUserIdAndIsCompletedTrue(1L)).willReturn(10L);
        given(progressRepository.findInProgressContents(1L, Pageable.unpaged()))
                .willReturn(new PageImpl<>(List.of(progress)));
        given(progressRepository.findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(1L, Pageable.unpaged()))
                .willReturn(new PageImpl<>(List.of(progress)));
        given(progressRepository.calculateTotalTimeSpent(1L)).willReturn(3600L);
        given(progressRepository.calculateAverageProgress(1L)).willReturn(75.0);
        given(progressRepository.calculateAverageQuizScore(1L)).willReturn(85.5);

        // when
        UserLearningStatsResponse response = contentProgressService.getUserLearningStats(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getNickname()).isEqualTo("테스트유저");
        assertThat(response.getCompletedContentCount()).isEqualTo(10L);
        assertThat(response.getTotalTimeSpentSeconds()).isEqualTo(3600L);
        assertThat(response.getTotalTimeSpentMinutes()).isEqualTo(60L);
        assertThat(response.getTotalTimeSpentHours()).isEqualTo(1L);
        assertThat(response.getAverageProgress()).isEqualTo(75.0);
        assertThat(response.getAverageQuizScore()).isEqualTo(85.5);
        assertThat(response.getLearningLevel()).isEqualTo("학습자"); // 10 completed
        assertThat(response.getContentsToNextLevel()).isEqualTo(5); // 15 - 10
    }

    @Test
    @DisplayName("학습 통계 조회 - 사용자 없음")
    void getUserLearningStats_UserNotFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contentProgressService.getUserLearningStats(999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}
