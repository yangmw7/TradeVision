package com.tradevision.dto.request;

import com.tradevision.constant.InvestmentLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 * 사용자 등록 시 필요한 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    /**
     * 이메일 (로그인 ID)
     */
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    /**
     * 비밀번호 (8자 이상, 영문+숫자 조합)
     */
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
        message = "비밀번호는 영문과 숫자를 포함해야 합니다"
    )
    private String password;

    /**
     * 닉네임
     */
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 50, message = "닉네임은 2~50자 사이여야 합니다")
    private String nickname;

    /**
     * 투자 경험 수준 (선택, 기본값: BEGINNER)
     */
    private InvestmentLevel investmentLevel;
}
