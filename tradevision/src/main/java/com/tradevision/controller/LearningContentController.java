package com.tradevision.controller;

import com.tradevision.constant.ContentType;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.dto.*;
import com.tradevision.repository.UserRepository;
import com.tradevision.service.ContentProgressService;
import com.tradevision.service.LearningContentService;
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

import java.util.List;

/**
 * 학습 콘텐츠 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningContentController {

    private final LearningContentService learningContentService;
    private final ContentProgressService progressService;
    private final UserRepository userRepository;

    /**
     * 모든 모듈 조회
     *
     * @param difficulty     난이도 필터 (선택)
     * @param category       카테고리 필터 (선택)
     * @param pageable       페이징 정보
     * @param authentication 인증 정보 (선택)
     * @return 모듈 목록
     */
    @GetMapping("/modules")
    public ResponseEntity<ModuleListResponse> getAllModules(
            @RequestParam(required = false) InvestmentLevel difficulty,
            @RequestParam(required = false) TechniqueCategory category,
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {

        log.info("모듈 목록 조회 - 난이도: {}, 카테고리: {}", difficulty, category);

        Long userId = extractUserIdFromAuth(authentication);
        ModuleListResponse response = learningContentService.getAllModules(difficulty, category, pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 모듈 상세 조회
     *
     * @param id             모듈 ID
     * @param authentication 인증 정보 (선택)
     * @return 모듈 상세
     */
    @GetMapping("/modules/{id}")
    public ResponseEntity<ModuleResponse> getModuleById(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("모듈 상세 조회 - ID: {}", id);

        Long userId = extractUserIdFromAuth(authentication);
        ModuleResponse response = learningContentService.getModuleById(id, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 필수 모듈 조회
     *
     * @param pageable       페이징 정보
     * @param authentication 인증 정보 (선택)
     * @return 필수 모듈 목록
     */
    @GetMapping("/modules/required")
    public ResponseEntity<ModuleListResponse> getRequiredModules(
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {

        log.info("필수 모듈 조회");

        Long userId = extractUserIdFromAuth(authentication);
        ModuleListResponse response = learningContentService.getRequiredModules(pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 모듈별 콘텐츠 조회
     *
     * @param moduleId       모듈 ID
     * @param pageable       페이징 정보
     * @param authentication 인증 정보 (선택)
     * @return 콘텐츠 목록
     */
    @GetMapping("/modules/{moduleId}/contents")
    public ResponseEntity<ContentListResponse> getContentsByModule(
            @PathVariable Long moduleId,
            @PageableDefault(size = 50, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {

        log.info("모듈별 콘텐츠 조회 - 모듈 ID: {}", moduleId);

        Long userId = extractUserIdFromAuth(authentication);
        ContentListResponse response = learningContentService.getContentsByModule(moduleId, pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 상세 조회
     *
     * @param id             콘텐츠 ID
     * @param authentication 인증 정보 (선택)
     * @return 콘텐츠 상세
     */
    @GetMapping("/contents/{id}")
    public ResponseEntity<ContentResponse> getContentById(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("콘텐츠 상세 조회 - ID: {}", id);

        Long userId = extractUserIdFromAuth(authentication);
        ContentResponse response = learningContentService.getContentById(id, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 타입별 조회
     *
     * @param contentType    콘텐츠 타입
     * @param pageable       페이징 정보
     * @param authentication 인증 정보 (선택)
     * @return 콘텐츠 목록
     */
    @GetMapping("/contents/type/{contentType}")
    public ResponseEntity<ContentListResponse> getContentsByType(
            @PathVariable ContentType contentType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        log.info("타입별 콘텐츠 조회 - 타입: {}", contentType);

        Long userId = extractUserIdFromAuth(authentication);
        ContentListResponse response = learningContentService.getContentsByType(contentType, pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 무료 콘텐츠 조회
     *
     * @param pageable       페이징 정보
     * @param authentication 인증 정보 (선택)
     * @return 콘텐츠 목록
     */
    @GetMapping("/contents/free")
    public ResponseEntity<ContentListResponse> getFreeContents(
            @PageableDefault(size = 20, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        log.info("무료 콘텐츠 조회");

        Long userId = extractUserIdFromAuth(authentication);
        ContentListResponse response = learningContentService.getFreeContents(pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 인기 콘텐츠 조회
     *
     * @param limit          조회 개수 (기본 10개)
     * @param authentication 인증 정보 (선택)
     * @return 인기 콘텐츠 목록
     */
    @GetMapping("/contents/popular")
    public ResponseEntity<List<ContentResponse>> getPopularContents(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            Authentication authentication) {

        log.info("인기 콘텐츠 조회 - Top {}", limit);

        Long userId = extractUserIdFromAuth(authentication);
        List<ContentResponse> response = learningContentService.getPopularContents(
                Pageable.ofSize(limit), userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 검색
     *
     * @param keyword        검색어
     * @param pageable       페이징 정보
     * @param authentication 인증 정보 (선택)
     * @return 검색 결과
     */
    @GetMapping("/contents/search")
    public ResponseEntity<ContentListResponse> searchContents(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        log.info("콘텐츠 검색 - 키워드: {}", keyword);

        Long userId = extractUserIdFromAuth(authentication);
        ContentListResponse response = learningContentService.searchContents(keyword, pageable, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 진행도 업데이트 (로그인 필수)
     *
     * @param id             콘텐츠 ID
     * @param request        진행도 요청
     * @param authentication 인증 정보
     * @return 진행도 응답
     */
    @PostMapping("/contents/{id}/progress")
    public ResponseEntity<ContentProgressResponse> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody ContentProgressRequest request,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("진행도 업데이트 - 사용자: {}, 콘텐츠: {}, 진행률: {}%",
                userId, id, request.getProgressPercentage());

        ContentProgressResponse response = progressService.updateProgress(id, userId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 좋아요 토글 (로그인 필수)
     *
     * @param id             콘텐츠 ID
     * @param authentication 인증 정보
     * @return 진행도 응답
     */
    @PostMapping("/contents/{id}/like")
    public ResponseEntity<ContentProgressResponse> toggleLike(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("좋아요 토글 - 사용자: {}, 콘텐츠: {}", userId, id);

        ContentProgressResponse response = progressService.toggleLike(id, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 북마크 토글 (로그인 필수)
     *
     * @param id             콘텐츠 ID
     * @param authentication 인증 정보
     * @return 진행도 응답
     */
    @PostMapping("/contents/{id}/bookmark")
    public ResponseEntity<ContentProgressResponse> toggleBookmark(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("북마크 토글 - 사용자: {}, 콘텐츠: {}", userId, id);

        ContentProgressResponse response = progressService.toggleBookmark(id, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 완료 처리 (로그인 필수)
     *
     * @param id             콘텐츠 ID
     * @param authentication 인증 정보
     * @return 진행도 응답
     */
    @PostMapping("/contents/{id}/complete")
    public ResponseEntity<ContentProgressResponse> markAsCompleted(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("콘텐츠 완료 처리 - 사용자: {}, 콘텐츠: {}", userId, id);

        ContentProgressResponse response = progressService.markAsCompleted(id, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 진행도 목록 조회 (로그인 필수)
     *
     * @param pageable       페이징 정보
     * @param authentication 인증 정보
     * @return 진행도 목록
     */
    @GetMapping("/progress")
    public ResponseEntity<Page<ContentProgressResponse>> getUserProgress(
            @PageableDefault(size = 20, sort = "lastAccessedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("사용자 진행도 목록 조회 - 사용자: {}", userId);

        Page<ContentProgressResponse> response = progressService.getUserProgress(userId, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 완료된 콘텐츠 조회 (로그인 필수)
     *
     * @param pageable       페이징 정보
     * @param authentication 인증 정보
     * @return 완료된 콘텐츠 목록
     */
    @GetMapping("/progress/completed")
    public ResponseEntity<Page<ContentProgressResponse>> getCompletedContents(
            @PageableDefault(size = 20, sort = "completedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("완료된 콘텐츠 조회 - 사용자: {}", userId);

        Page<ContentProgressResponse> response = progressService.getCompletedContents(userId, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 진행중인 콘텐츠 조회 (로그인 필수)
     *
     * @param pageable       페이징 정보
     * @param authentication 인증 정보
     * @return 진행중인 콘텐츠 목록
     */
    @GetMapping("/progress/in-progress")
    public ResponseEntity<Page<ContentProgressResponse>> getInProgressContents(
            @PageableDefault(size = 20, sort = "lastAccessedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("진행중인 콘텐츠 조회 - 사용자: {}", userId);

        Page<ContentProgressResponse> response = progressService.getInProgressContents(userId, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 북마크된 콘텐츠 조회 (로그인 필수)
     *
     * @param pageable       페이징 정보
     * @param authentication 인증 정보
     * @return 북마크된 콘텐츠 목록
     */
    @GetMapping("/progress/bookmarked")
    public ResponseEntity<Page<ContentProgressResponse>> getBookmarkedContents(
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("북마크된 콘텐츠 조회 - 사용자: {}", userId);

        Page<ContentProgressResponse> response = progressService.getBookmarkedContents(userId, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 학습 통계 조회 (로그인 필수)
     *
     * @param authentication 인증 정보
     * @return 학습 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<UserLearningStatsResponse> getUserLearningStats(Authentication authentication) {

        Long userId = extractUserIdFromAuthRequired(authentication);
        log.info("사용자 학습 통계 조회 - 사용자: {}", userId);

        UserLearningStatsResponse response = progressService.getUserLearningStats(userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Authentication 객체에서 사용자 ID 추출 (선택적)
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
     * Authentication 객체에서 사용자 ID 추출 (필수)
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
