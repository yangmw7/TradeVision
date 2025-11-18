package com.tradevision.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커뮤니티 게시글 엔티티
 * 사용자들의 차트 분석 및 아이디어 공유
 */
@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 게시글 내용
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 차트 이미지 URL
     */
    @Column(length = 500)
    private String chartImageUrl;

    /**
     * 작성자 이름
     */
    @Column(nullable = false, length = 50)
    private String author;

    /**
     * 카테고리 (예: 주식, 선물, 옵션, 코인 등)
     */
    @Column(length = 50)
    private String category;

    /**
     * 좋아요 수
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer likes = 0;

    /**
     * 조회수
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer views = 0;

    /**
     * 공개 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = true;

    /**
     * 좋아요 증가
     */
    public void incrementLikes() {
        this.likes++;
    }

    /**
     * 조회수 증가
     */
    public void incrementViews() {
        this.views++;
    }
}
