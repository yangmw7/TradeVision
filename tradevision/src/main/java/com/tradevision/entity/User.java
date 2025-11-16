package com.tradevision.entity;

import com.tradevision.constant.InvestmentLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 * TradeVision 플랫폼 사용자 정보 저장
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    /**
     * 사용자 고유 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이메일 (로그인 ID, 중복 불가)
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * BCrypt 암호화된 비밀번호
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 닉네임
     */
    @Column(nullable = false, length = 50)
    private String nickname;

    /**
     * 투자 경험 수준
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "investment_level", nullable = false, length = 20)
    @Builder.Default
    private InvestmentLevel investmentLevel = InvestmentLevel.BEGINNER;

    /**
     * 비밀번호 변경
     * @param encodedPassword BCrypt 암호화된 새 비밀번호
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 프로필 업데이트
     * @param nickname 새 닉네임
     * @param investmentLevel 새 투자 경험 수준
     */
    public void updateProfile(String nickname, InvestmentLevel investmentLevel) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (investmentLevel != null) {
            this.investmentLevel = investmentLevel;
        }
    }
}
