package com.tradevision.service;

import com.tradevision.constant.ContentType;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.dto.ContentListResponse;
import com.tradevision.dto.ContentResponse;
import com.tradevision.dto.ModuleResponse;
import com.tradevision.entity.ContentModule;
import com.tradevision.entity.LearningContent;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.repository.ContentModuleRepository;
import com.tradevision.repository.LearningContentRepository;
import com.tradevision.repository.UserContentProgressRepository;
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
 * LearningContentService 테스트
 */
@ExtendWith(MockitoExtension.class)
class LearningContentServiceTest {

    @Mock
    private ContentModuleRepository moduleRepository;

    @Mock
    private LearningContentRepository contentRepository;

    @Mock
    private UserContentProgressRepository progressRepository;

    @InjectMocks
    private LearningContentService learningContentService;

    private ContentModule module;
    private LearningContent content;

    @BeforeEach
    void setUp() {
        module = ContentModule.builder()
                .id(1L)
                .title("이동평균선 학습")
                .titleEn("Moving Average Learning")
                .description("이동평균선 기초부터 실전까지")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.MOVING_AVERAGE)
                .displayOrder(1)
                .estimatedDurationMinutes(30)
                .isRequired(true)
                .isActive(true)
                .build();

        content = LearningContent.builder()
                .id(1L)
                .module(module)
                .title("이동평균선이란?")
                .titleEn("What is Moving Average?")
                .summary("이동평균선의 기본 개념")
                .contentBody("# 이동평균선\n\n이동평균선은...")
                .contentType(ContentType.ARTICLE)
                .displayOrder(1)
                .estimatedDurationMinutes(10)
                .isFree(true)
                .isActive(true)
                .viewCount(100L)
                .likeCount(50L)
                .build();
    }

    @Test
    @DisplayName("모듈 상세 조회 - 성공")
    void getModuleById_Success() {
        // given
        given(moduleRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(module));
        given(contentRepository.countByModuleIdAndIsActiveTrue(1L)).willReturn(5L);

        // when
        ModuleResponse response = learningContentService.getModuleById(1L, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("이동평균선 학습");
        assertThat(response.getContentCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("모듈 상세 조회 - 실패 (모듈 없음)")
    void getModuleById_NotFound() {
        // given
        given(moduleRepository.findByIdAndIsActiveTrue(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> learningContentService.getModuleById(999L, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LEARNING_CONTENT_NOT_FOUND);
    }

    @Test
    @DisplayName("모듈별 콘텐츠 조회 - 성공")
    void getContentsByModule_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LearningContent> contentPage = new PageImpl<>(List.of(content), pageable, 1);

        given(moduleRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(module));
        given(contentRepository.findByModuleIdAndIsActiveTrue(1L, pageable)).willReturn(contentPage);

        // when
        ContentListResponse response = learningContentService.getContentsByModule(1L, pageable, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().get(0).getTitle()).isEqualTo("이동평균선이란?");
    }

    @Test
    @DisplayName("콘텐츠 상세 조회 - 성공")
    void getContentById_Success() {
        // given
        given(contentRepository.findByIdAndIsActiveTrue(1L)).willReturn(Optional.of(content));

        // when
        ContentResponse response = learningContentService.getContentById(1L, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("이동평균선이란?");
        assertThat(response.getContentType()).isEqualTo(ContentType.ARTICLE);
        verify(contentRepository).incrementViewCount(1L);
    }

    @Test
    @DisplayName("콘텐츠 상세 조회 - 실패 (콘텐츠 없음)")
    void getContentById_NotFound() {
        // given
        given(contentRepository.findByIdAndIsActiveTrue(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> learningContentService.getContentById(999L, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LEARNING_CONTENT_NOT_FOUND);
    }

    @Test
    @DisplayName("콘텐츠 검색 - 성공")
    void searchContents_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LearningContent> contentPage = new PageImpl<>(List.of(content), pageable, 1);

        given(contentRepository.searchByKeyword("이동평균", pageable)).willReturn(contentPage);

        // when
        ContentListResponse response = learningContentService.searchContents("이동평균", pageable, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().get(0).getTitle()).contains("이동평균");
    }

    @Test
    @DisplayName("인기 콘텐츠 조회 - 성공")
    void getPopularContents_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        given(contentRepository.findTopByViewCount(pageable)).willReturn(List.of(content));

        // when
        List<ContentResponse> response = learningContentService.getPopularContents(pageable, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getViewCount()).isEqualTo(100L);
    }
}
