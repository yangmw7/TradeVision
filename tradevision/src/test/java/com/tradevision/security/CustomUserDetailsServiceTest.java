package com.tradevision.security;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.entity.User;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.ResourceNotFoundException;
import com.tradevision.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * CustomUserDetailsService 단위 테스트
 * Spring Security UserDetailsService 구현체 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService 테스트")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword123")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
    }

    @Test
    @DisplayName("이메일로 사용자 로드 성공")
    void loadUserByUsername_Success() {
        // given
        given(userRepository.findByEmail("test@example.com"))
                .willReturn(Optional.of(testUser));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword123");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 UsernameNotFoundException 발생")
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // given
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("notexist@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");

        verify(userRepository).findByEmail("notexist@example.com");
    }

    @Test
    @DisplayName("UserDetails 권한 확인 - ROLE_USER")
    void loadUserByUsername_CheckAuthorities() {
        // given
        given(userRepository.findByEmail("test@example.com"))
                .willReturn(Optional.of(testUser));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // then
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }
}
