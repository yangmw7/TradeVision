package com.tradevision.security;

import com.tradevision.entity.User;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.ResourceNotFoundException;
import com.tradevision.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Spring Security UserDetailsService 구현
 * 사용자 인증을 위한 사용자 정보 로드
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 이메일(username)로 사용자 정보 로드
     * Spring Security가 인증 시 호출
     *
     * @param email 사용자 이메일
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "이메일에 해당하는 사용자를 찾을 수 없습니다: " + email
                ));

        // Spring Security User 객체 생성
        // TradeVision은 단순 사용자/관리자 구분 없이 모든 사용자 동일 권한
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()  // authorities (권한) - 현재는 빈 리스트
        );
    }
}
