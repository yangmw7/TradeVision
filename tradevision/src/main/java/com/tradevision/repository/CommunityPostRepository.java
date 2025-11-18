package com.tradevision.repository;

import com.tradevision.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커뮤니티 게시글 리포지토리
 */
@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    /**
     * 공개된 게시글 조회 (최신순)
     */
    Page<CommunityPost> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 카테고리별 공개 게시글 조회
     */
    Page<CommunityPost> findByIsPublicTrueAndCategoryOrderByCreatedAtDesc(String category, Pageable pageable);

    /**
     * 인기 게시글 조회 (좋아요 많은 순)
     */
    @Query("SELECT p FROM CommunityPost p WHERE p.isPublic = true ORDER BY p.likes DESC, p.createdAt DESC")
    List<CommunityPost> findTopByOrderByLikesDesc(Pageable pageable);
}
