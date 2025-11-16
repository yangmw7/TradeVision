package com.tradevision.service;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.dto.RecommendationResponse;
import com.tradevision.dto.TechniqueResponse;
import com.tradevision.entity.TradingTechnique;
import com.tradevision.entity.User;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.repository.TradingTechniqueRepository;
import com.tradevision.repository.UserRepository;
import com.tradevision.repository.UserTechniqueProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 매매기법 추천 서비스
 * 사용자의 투자 수준과 학습 진행도를 기반으로 맞춤형 기법 추천
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserRepository userRepository;
    private final TradingTechniqueRepository techniqueRepository;
    private final UserTechniqueProgressRepository progressRepository;

    /**
     * 사용자 맞춤형 기법 추천
     *
     * @param userId 사용자 ID
     * @param limit  추천 개수 (기본 5개)
     * @return 추천 기법 목록
     */
    public RecommendationResponse getPersonalizedRecommendations(Long userId, Integer limit) {
        log.info("맞춤형 기법 추천 - 사용자 ID: {}, 추천 개수: {}", userId, limit);

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        InvestmentLevel userLevel = user.getInvestmentLevel();
        int recommendationLimit = (limit != null && limit > 0) ? limit : 5;

        // 사용자 학습 통계
        long completedCount = progressRepository.countByUserIdAndIsCompletedTrue(userId);
        Double averageProgress = progressRepository.calculateAverageProgress(userId);
        if (averageProgress == null) {
            averageProgress = 0.0;
        }

        // 추천 기법 조회
        List<TradingTechnique> recommendedTechniques = getRecommendedTechniques(
                userId, userLevel, completedCount, PageRequest.of(0, recommendationLimit)
        );

        // 추천 메시지 생성
        String reason = generateRecommendationReason(userLevel, completedCount, averageProgress);
        String message = generateRecommendationMessage(userLevel, completedCount);

        // 응답 빌드
        return RecommendationResponse.builder()
                .recommendedTechniques(recommendedTechniques.stream()
                        .map(this::buildTechniqueResponse)
                        .toList())
                .reason(reason)
                .userLevel(userLevel.getDisplayName())
                .completedCount(completedCount)
                .averageProgress(Math.round(averageProgress * 10.0) / 10.0) // 소수점 첫째자리
                .message(message)
                .build();
    }

    /**
     * 추천 기법 조회 로직
     * 사용자 수준과 학습 진행도를 고려한 추천
     */
    private List<TradingTechnique> getRecommendedTechniques(
            Long userId, InvestmentLevel userLevel, long completedCount, PageRequest pageRequest) {

        // 1. 초보자이거나 완료한 기법이 없는 경우: 해당 난이도의 인기 기법 추천
        if (userLevel == InvestmentLevel.BEGINNER || completedCount == 0) {
            log.info("초보자 또는 미학습자 - 기본 난이도 기법 추천");
            return techniqueRepository.findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                    userLevel, pageRequest
            ).getContent();
        }

        // 2. 현재 수준의 기법을 많이 완료한 경우 (50% 이상): 다음 난이도 추천
        long currentLevelCount = progressRepository.countByUserIdAndStatus(userId,
                com.tradevision.constant.ProgressStatus.COMPLETED);

        if (completedCount >= 3 && currentLevelCount >= 2) {
            InvestmentLevel nextLevel = getNextLevel(userLevel);
            if (nextLevel != userLevel) {
                log.info("다음 난이도 추천 - 현재: {}, 다음: {}", userLevel, nextLevel);
                return techniqueRepository.findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                        nextLevel, pageRequest
                ).getContent();
            }
        }

        // 3. 기본: 현재 수준의 추천 기법
        log.info("현재 난이도 추천 - 난이도: {}", userLevel);
        return techniqueRepository.findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                userLevel, pageRequest
        ).getContent();
    }

    /**
     * 다음 난이도 반환
     */
    private InvestmentLevel getNextLevel(InvestmentLevel currentLevel) {
        return switch (currentLevel) {
            case BEGINNER -> InvestmentLevel.INTERMEDIATE;
            case INTERMEDIATE -> InvestmentLevel.ADVANCED;
            case ADVANCED -> InvestmentLevel.ADVANCED; // 최고 레벨
        };
    }

    /**
     * 추천 이유 생성
     */
    private String generateRecommendationReason(InvestmentLevel userLevel, long completedCount, Double averageProgress) {
        if (completedCount == 0) {
            return String.format("%s 투자자를 위한 기본 매매기법부터 시작하세요", userLevel.getDisplayName());
        }

        if (completedCount >= 3 && averageProgress >= 70.0) {
            InvestmentLevel nextLevel = getNextLevel(userLevel);
            if (nextLevel != userLevel) {
                return String.format("훌륭합니다! %d개 기법을 완료하셨네요. %s 난이도에 도전해보세요",
                        completedCount, nextLevel.getDisplayName());
            }
        }

        return String.format("현재 %s 수준에 맞춤형 추천입니다. 평균 진행률 %.1f%%",
                userLevel.getDisplayName(), averageProgress);
    }

    /**
     * 추천 메시지 생성
     */
    private String generateRecommendationMessage(InvestmentLevel userLevel, long completedCount) {
        if (completedCount == 0) {
            return "첫 번째 매매기법 학습을 시작해보세요! 기초부터 차근차근 배워나가면 성공적인 투자자가 될 수 있습니다.";
        }

        if (completedCount >= 5) {
            return "대단합니다! 꾸준한 학습으로 투자 실력이 향상되고 있습니다. 계속해서 새로운 기법을 익혀보세요.";
        }

        if (completedCount >= 3) {
            return "좋은 진행입니다! 학습한 기법들을 실전에 적용하며 경험을 쌓아가세요.";
        }

        return "매매기법 학습을 꾸준히 이어가세요. 각 기법의 장단점을 이해하면 시장 상황에 맞게 활용할 수 있습니다.";
    }

    /**
     * TechniqueResponse 변환
     */
    private TechniqueResponse buildTechniqueResponse(TradingTechnique technique) {
        return TechniqueResponse.builder()
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
                .updatedAt(technique.getUpdatedAt())
                .build();
    }
}
