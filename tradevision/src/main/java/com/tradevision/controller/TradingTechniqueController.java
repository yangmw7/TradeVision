package com.tradevision.controller;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.ProgressStatus;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.dto.*;
import com.tradevision.repository.UserRepository;
import com.tradevision.service.RecommendationService;
import com.tradevision.service.TradingTechniqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 매매기법 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/techniques")
@RequiredArgsConstructor
public class TradingTechniqueController {

    private final TradingTechniqueService techniqueService;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    /**
     * 모든 기법 조회 (필터링 가능)
     *
     * @param difficulty 난이도 (선택)
     * @param category   카테고리 (선택)
     * @param pageable   페이징 정보
     * @param principal  인증된 사용자 (선택)
     * @return 기법 목록
     */
    @GetMapping
    public ResponseEntity<TechniqueListResponse> getAllTechniques(
            @RequestParam(required = false) InvestmentLevel difficulty,
            @RequestParam(required = false) TechniqueCategory category,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        log.info("기법 목록 조회 - 난이도: {}, 카테고리: {}, 페이지: {}",
                difficulty, category, pageable.getPageNumber());

        Long userId = extractUserIdFromAuth(authentication);

        TechniqueListResponse response;

        // 필터 조건에 따라 다른 서비스 메서드 호출
        if (difficulty != null && category != null) {
            response = techniqueService.getTechniquesByDifficultyAndCategory(difficulty, category, pageable, userId);
        } else if (difficulty != null) {
            response = techniqueService.getTechniquesByDifficulty(difficulty, pageable, userId);
        } else if (category != null) {
            response = techniqueService.getTechniquesByCategory(category, pageable, userId);
        } else {
            response = techniqueService.getAllTechniques(pageable, userId);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 기법 상세 조회
     *
     * @param id        기법 ID
     * @param principal 인증된 사용자 (선택)
     * @return 기법 상세
     */
    @GetMapping("/{id}")
    public ResponseEntity<TechniqueResponse> getTechniqueById(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("기법 상세 조회 - ID: {}", id);

        Long userId = extractUserIdFromAuth(authentication);
        TechniqueResponse response = techniqueService.getTechniqueById(id, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 기법 검색
     *
     * @param keyword   검색어
     * @param pageable  페이징 정보
     * @param principal 인증된 사용자 (선택)
     * @return 검색 결과
     */
    @GetMapping("/search")
    public ResponseEntity<TechniqueListResponse> searchTechniques(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        log.info("기법 검색 - 키워드: {}", keyword);

        Long userId = extractUserIdFromAuth(authentication);
        TechniqueListResponse response = techniqueService.searchTechniques(keyword, pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 인기 기법 조회
     *
     * @param pageable  페이징 정보
     * @param principal 인증된 사용자 (선택)
     * @return 인기 기법 목록
     */
    @GetMapping("/popular")
    public ResponseEntity<TechniqueListResponse> getPopularTechniques(
            @PageableDefault(size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        log.info("인기 기법 조회");

        Long userId = extractUserIdFromAuth(authentication);
        TechniqueListResponse response = techniqueService.getPopularTechniques(pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 맞춤형 기법 추천 (로그인 필수)
     *
     * @param limit     추천 개수 (기본 5개)
     * @param principal 인증된 사용자
     * @return 추천 기법 목록
     */
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam(required = false, defaultValue = "5") Integer limit,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("맞춤형 추천 조회 - 사용자 ID: {}, 추천 개수: {}", userId, limit);

        RecommendationResponse response = recommendationService.getPersonalizedRecommendations(userId, limit);

        return ResponseEntity.ok(response);
    }

    /**
     * 학습 진행도 업데이트 (로그인 필수)
     *
     * @param id        기법 ID
     * @param request   진행도 요청
     * @param principal 인증된 사용자
     * @return 진행도 응답
     */
    @PostMapping("/{id}/progress")
    public ResponseEntity<ProgressResponse> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody ProgressRequest request,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("진행도 업데이트 - 사용자: {}, 기법: {}, 진행률: {}%",
                userId, id, request.getProgressPercentage());

        ProgressResponse response = techniqueService.updateProgress(id, userId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 진행도 목록 조회 (로그인 필수)
     *
     * @param status    진행 상태 필터 (선택)
     * @param pageable  페이징 정보
     * @param principal 인증된 사용자
     * @return 진행도 목록
     */
    @GetMapping("/progress")
    public ResponseEntity<Page<ProgressResponse>> getUserProgress(
            @RequestParam(required = false) ProgressStatus status,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("사용자 진행도 목록 조회 - 사용자: {}, 상태: {}", userId, status);

        Page<ProgressResponse> response;

        if (status != null) {
            response = techniqueService.getUserProgressByStatus(userId, status, pageable);
        } else {
            response = techniqueService.getUserProgressHistory(userId, pageable);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 기법의 진행도 조회 (로그인 필수)
     *
     * @param techniqueId 기법 ID
     * @param principal   인증된 사용자
     * @return 진행도 응답
     */
    @GetMapping("/progress/{techniqueId}")
    public ResponseEntity<ProgressResponse> getProgressByTechnique(
            @PathVariable Long techniqueId,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("특정 기법 진행도 조회 - 사용자: {}, 기법: {}", userId, techniqueId);

        ProgressResponse response = techniqueService.getUserProgress(techniqueId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Authentication 객체에서 사용자 ID 추출 (선택적 - 로그인하지 않은 경우 null 반환)
     *
     * @param authentication 인증 정보
     * @return 사용자 ID 또는 null
     */
    private Long extractUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .map(user -> user.getId())
                    .orElse(null);
        }

        return null;
    }

    /**
     * Authentication 객체에서 사용자 ID 추출 (필수 - 로그인하지 않은 경우 예외 발생)
     *
     * @param authentication 인증 정보
     * @return 사용자 ID
     * @throws IllegalStateException 인증 정보가 없거나 유효하지 않은 경우
     */
    private Long extractUserIdFromAuthRequired(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증이 필요합니다");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new IllegalStateException("유효하지 않은 사용자입니다"));
        }

        throw new IllegalStateException("유효하지 않은 인증 정보입니다");
    }
}
