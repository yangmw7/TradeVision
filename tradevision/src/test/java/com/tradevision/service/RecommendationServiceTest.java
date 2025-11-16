package com.tradevision.service;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.dto.RecommendationResponse;
import com.tradevision.entity.TradingTechnique;
import com.tradevision.entity.User;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

/**
 * RecommendationService 테스트
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradingTechniqueRepository techniqueRepository;

    @Mock
    private UserTechniqueProgressRepository progressRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private User user;
    private TradingTechnique technique;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();

        technique = TradingTechnique.builder()
                .id(1L)
                .name("이동평균선")
                .nameEn("Moving Average")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.MOVING_AVERAGE)
                .summary("기초 기법")
                .description("설명")
                .usageGuide("사용법")
                .riskLevel(2)
                .build();
    }

    @Test
    @DisplayName("맞춤형 추천 - 성공")
    void getPersonalizedRecommendations_Success() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.countByUserIdAndIsCompletedTrue(1L)).willReturn(0L);
        given(progressRepository.calculateAverageProgress(1L)).willReturn(0.0);

        Page<TradingTechnique> techniques = new PageImpl<>(List.of(technique));
        given(techniqueRepository.findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                any(InvestmentLevel.class), any(PageRequest.class))).willReturn(techniques);

        // when
        RecommendationResponse response = recommendationService.getPersonalizedRecommendations(1L, 5);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserLevel()).isEqualTo("초보자");
        assertThat(response.getCompletedCount()).isEqualTo(0);
        assertThat(response.getAverageProgress()).isEqualTo(0.0);
        assertThat(response.getRecommendedTechniques()).isNotEmpty();
        assertThat(response.getReason()).isNotNull();
        assertThat(response.getMessage()).isNotNull();
    }

    @Test
    @DisplayName("맞춤형 추천 - 진행도 있는 경우")
    void getPersonalizedRecommendations_WithProgress() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.countByUserIdAndIsCompletedTrue(1L)).willReturn(2L);
        given(progressRepository.calculateAverageProgress(1L)).willReturn(65.5);

        Page<TradingTechnique> techniques = new PageImpl<>(List.of(technique));
        given(techniqueRepository.findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                any(InvestmentLevel.class), any(PageRequest.class))).willReturn(techniques);

        // when
        RecommendationResponse response = recommendationService.getPersonalizedRecommendations(1L, 5);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCompletedCount()).isEqualTo(2);
        assertThat(response.getAverageProgress()).isEqualTo(65.5);
        assertThat(response.getRecommendedTechniques()).isNotEmpty();
    }

    @Test
    @DisplayName("맞춤형 추천 - 사용자 없음")
    void getPersonalizedRecommendations_UserNotFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> recommendationService.getPersonalizedRecommendations(999L, 5))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("맞춤형 추천 - 기본 개수 (null limit)")
    void getPersonalizedRecommendations_DefaultLimit() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(progressRepository.countByUserIdAndIsCompletedTrue(1L)).willReturn(0L);
        given(progressRepository.calculateAverageProgress(1L)).willReturn(0.0);

        Page<TradingTechnique> techniques = new PageImpl<>(List.of(technique));
        given(techniqueRepository.findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                any(InvestmentLevel.class), any(PageRequest.class))).willReturn(techniques);

        // when
        RecommendationResponse response = recommendationService.getPersonalizedRecommendations(1L, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getRecommendedTechniques()).isNotEmpty();
    }
}
