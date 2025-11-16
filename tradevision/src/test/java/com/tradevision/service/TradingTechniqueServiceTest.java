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
import static org.mockito.Mockito.*;

/**
 * TradingTechniqueService 테스트
 */
@ExtendWith(MockitoExtension.class)
class TradingTechniqueServiceTest {

    @Mock
    private TradingTechniqueRepository techniqueRepository;

    @Mock
    private UserTechniqueProgressRepository progressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TradingTechniqueService techniqueService;

    private TradingTechnique technique;
    private User user;
    private UserTechniqueProgress progress;

    @BeforeEach
    void setUp() {
        technique = TradingTechnique.builder()
                .id(1L)
                .name("이동평균선")
                .nameEn("Moving Average")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.MOVING_AVERAGE)
                .summary("기초 기법")
                .description("설명")
                .usageGuide("사용법")
                .exampleScenario("예시")
                .advantages("장점")
                .disadvantages("단점")
                .riskLevel(2)
                .isActive(true)
                .viewCount(100L)
                .recommendationCount(50L)
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();

        progress = UserTechniqueProgress.builder()
                .id(1L)
                .user(user)
                .technique(technique)
                .status(ProgressStatus.IN_PROGRESS)
                .progressPercentage(50)
                .isBookmarked(false)
                .isCompleted(false)
                .build();
    }

    @Test
    @DisplayName("모든 기법 조회 - 성공")
    void getAllTechniques_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TradingTechnique> techniquePage = new PageImpl<>(List.of(technique), pageable, 1);
        given(techniqueRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)).willReturn(techniquePage);

        // when
        TechniqueListResponse response = techniqueService.getAllTechniques(pageable, null);

        // then
        assertThat(response.getTechniques()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getTechniques().get(0).getName()).isEqualTo("이동평균선");
    }

    @Test
    @DisplayName("기법 상세 조회 - 성공")
    void getTechniqueById_Success() {
        // given
        given(techniqueRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(technique));

        // when
        TechniqueResponse response = techniqueService.getTechniqueById(1L, null);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("이동평균선");
        verify(techniqueRepository).incrementViewCount(1L);
    }

    @Test
    @DisplayName("기법 상세 조회 - 실패 (기법 없음)")
    void getTechniqueById_NotFound() {
        // given
        given(techniqueRepository.findByIdAndIsActiveTrue(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> techniqueService.getTechniqueById(999L, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TECHNIQUE_NOT_FOUND);
    }

    @Test
    @DisplayName("기법 상세 조회 - 사용자 진행도 포함")
    void getTechniqueById_WithUserProgress() {
        // given
        given(techniqueRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(technique));
        given(progressRepository.findByUserIdAndTechniqueId(1L, 1L)).willReturn(Optional.of(progress));

        // when
        TechniqueResponse response = techniqueService.getTechniqueById(1L, 1L);

        // then
        assertThat(response.getUserProgress()).isNotNull();
        assertThat(response.getUserProgress().getProgressPercentage()).isEqualTo(50);
        assertThat(response.getUserProgress().getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("난이도별 기법 조회")
    void getTechniquesByDifficulty() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TradingTechnique> techniquePage = new PageImpl<>(List.of(technique), pageable, 1);
        given(techniqueRepository.findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                InvestmentLevel.BEGINNER, pageable)).willReturn(techniquePage);

        // when
        TechniqueListResponse response = techniqueService.getTechniquesByDifficulty(
                InvestmentLevel.BEGINNER, pageable, null);

        // then
        assertThat(response.getTechniques()).hasSize(1);
        assertThat(response.getTechniques().get(0).getDifficultyLevel()).isEqualTo(InvestmentLevel.BEGINNER);
    }

    @Test
    @DisplayName("카테고리별 기법 조회")
    void getTechniquesByCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TradingTechnique> techniquePage = new PageImpl<>(List.of(technique), pageable, 1);
        given(techniqueRepository.findByCategoryAndIsActiveTrueOrderByViewCountDesc(
                TechniqueCategory.MOVING_AVERAGE, pageable)).willReturn(techniquePage);

        // when
        TechniqueListResponse response = techniqueService.getTechniquesByCategory(
                TechniqueCategory.MOVING_AVERAGE, pageable, null);

        // then
        assertThat(response.getTechniques()).hasSize(1);
        assertThat(response.getTechniques().get(0).getCategory()).isEqualTo(TechniqueCategory.MOVING_AVERAGE);
    }

    @Test
    @DisplayName("키워드 검색")
    void searchTechniques() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TradingTechnique> techniquePage = new PageImpl<>(List.of(technique), pageable, 1);
        given(techniqueRepository.searchByKeyword("이동평균", pageable)).willReturn(techniquePage);

        // when
        TechniqueListResponse response = techniqueService.searchTechniques("이동평균", pageable, null);

        // then
        assertThat(response.getTechniques()).hasSize(1);
        assertThat(response.getTechniques().get(0).getName()).contains("이동평균");
    }

    @Test
    @DisplayName("진행도 업데이트 - 신규 생성")
    void updateProgress_CreateNew() {
        // given
        ProgressRequest request = ProgressRequest.builder()
                .status(ProgressStatus.IN_PROGRESS)
                .progressPercentage(30)
                .build();

        given(techniqueRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(technique));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.findByUserIdAndTechniqueId(1L, 1L)).willReturn(Optional.empty());
        given(progressRepository.save(any(UserTechniqueProgress.class))).willReturn(progress);

        // when
        ProgressResponse response = techniqueService.updateProgress(1L, 1L, request);

        // then
        assertThat(response).isNotNull();
        verify(progressRepository).save(any(UserTechniqueProgress.class));
    }

    @Test
    @DisplayName("진행도 업데이트 - 기존 업데이트")
    void updateProgress_UpdateExisting() {
        // given
        ProgressRequest request = ProgressRequest.builder()
                .status(ProgressStatus.COMPLETED)
                .progressPercentage(100)
                .userRating(5)
                .userNotes("완료!")
                .build();

        given(techniqueRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(technique));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.findByUserIdAndTechniqueId(1L, 1L)).willReturn(Optional.of(progress));
        given(progressRepository.save(any(UserTechniqueProgress.class))).willReturn(progress);

        // when
        ProgressResponse response = techniqueService.updateProgress(1L, 1L, request);

        // then
        assertThat(response).isNotNull();
        verify(progressRepository).save(any(UserTechniqueProgress.class));
    }

    @Test
    @DisplayName("진행도 업데이트 - 기법 없음")
    void updateProgress_TechniqueNotFound() {
        // given
        ProgressRequest request = ProgressRequest.builder()
                .status(ProgressStatus.IN_PROGRESS)
                .progressPercentage(30)
                .build();

        given(techniqueRepository.findByIdAndIsActiveTrue(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> techniqueService.updateProgress(999L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TECHNIQUE_NOT_FOUND);
    }

    @Test
    @DisplayName("진행도 업데이트 - 사용자 없음")
    void updateProgress_UserNotFound() {
        // given
        ProgressRequest request = ProgressRequest.builder()
                .status(ProgressStatus.IN_PROGRESS)
                .progressPercentage(30)
                .build();

        given(techniqueRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(technique));
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> techniqueService.updateProgress(1L, 999L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 진행도 조회")
    void getUserProgress() {
        // given
        given(progressRepository.findByUserIdAndTechniqueId(1L, 1L)).willReturn(Optional.of(progress));

        // when
        ProgressResponse response = techniqueService.getUserProgress(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getTechniqueId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("사용자 진행도 조회 - 진행도 없음")
    void getUserProgress_NotFound() {
        // given
        given(progressRepository.findByUserIdAndTechniqueId(1L, 999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> techniqueService.getUserProgress(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROGRESS_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 진행도 목록 조회")
    void getUserProgressHistory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserTechniqueProgress> progressPage = new PageImpl<>(List.of(progress), pageable, 1);
        given(progressRepository.findByUserIdOrderByUpdatedAtDesc(1L, pageable)).willReturn(progressPage);

        // when
        Page<ProgressResponse> response = techniqueService.getUserProgressHistory(1L, pageable);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("사용자 상태별 진행도 조회")
    void getUserProgressByStatus() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserTechniqueProgress> progressPage = new PageImpl<>(List.of(progress), pageable, 1);
        given(progressRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(
                1L, ProgressStatus.IN_PROGRESS, pageable)).willReturn(progressPage);

        // when
        Page<ProgressResponse> response = techniqueService.getUserProgressByStatus(
                1L, ProgressStatus.IN_PROGRESS, pageable);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getStatus()).isEqualTo(ProgressStatus.IN_PROGRESS);
    }
}
