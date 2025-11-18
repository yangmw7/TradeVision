package com.tradevision.controller;

import com.tradevision.dto.CommunityPostResponse;
import com.tradevision.dto.response.ApiResponse;
import com.tradevision.entity.CommunityPost;
import com.tradevision.repository.CommunityPostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 커뮤니티 API 컨트롤러
 * 사용자 차트 분석 및 아이디어 공유 기능
 */
@Tag(name = "Community", description = "커뮤니티 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPostRepository communityPostRepository;

    /**
     * 공개 커뮤니티 게시글 목록 조회
     * GET /api/community/posts
     */
    @Operation(summary = "커뮤니티 게시글 목록 조회", description = "공개된 커뮤니티 게시글 목록을 조회합니다")
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<CommunityPostResponse>>> getPosts(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("커뮤니티 게시글 목록 조회 - 카테고리: {}", category);

        Page<CommunityPost> posts;
        if (category != null && !category.isEmpty()) {
            posts = communityPostRepository.findByIsPublicTrueAndCategoryOrderByCreatedAtDesc(category, pageable);
        } else {
            posts = communityPostRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
        }

        Page<CommunityPostResponse> response = posts.map(CommunityPostResponse::from);

        return ResponseEntity.ok(
                ApiResponse.success("커뮤니티 게시글 조회 성공", response)
        );
    }

    /**
     * 인기 커뮤니티 게시글 조회
     * GET /api/community/posts/popular
     */
    @Operation(summary = "인기 게시글 조회", description = "좋아요가 많은 인기 게시글을 조회합니다")
    @GetMapping("/posts/popular")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getPopularPosts(
            @RequestParam(defaultValue = "8") int limit) {

        log.info("인기 커뮤니티 게시글 조회 - Top {}", limit);

        List<CommunityPost> posts = communityPostRepository.findTopByOrderByLikesDesc(
                Pageable.ofSize(limit)
        );

        List<CommunityPostResponse> response = posts.stream()
                .map(CommunityPostResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("인기 게시글 조회 성공", response)
        );
    }

    /**
     * 특정 게시글 상세 조회
     * GET /api/community/posts/{id}
     */
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다")
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> getPostById(@PathVariable Long id) {

        log.info("커뮤니티 게시글 상세 조회 - ID: {}", id);

        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다"));

        // 조회수 증가
        post.incrementViews();
        communityPostRepository.save(post);

        CommunityPostResponse response = CommunityPostResponse.from(post);

        return ResponseEntity.ok(
                ApiResponse.success("게시글 조회 성공", response)
        );
    }
}
