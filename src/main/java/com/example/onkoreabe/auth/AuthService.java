package com.example.onkoreabe.auth;

import com.example.onkoreabe.auth.dto.AuthResponse;
import com.example.onkoreabe.auth.dto.LoginRequest;
import com.example.onkoreabe.auth.dto.SignupRequest;
import com.example.onkoreabe.auth.jwt.JwtTokenProvider;
import com.example.onkoreabe.user.LoginType;
import com.example.onkoreabe.user.Role;
import com.example.onkoreabe.user.User;
import com.example.onkoreabe.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        log.info("회원가입 요청: email={}", request.getEmail());

        // 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .role(Role.USER)
                .loginType(LoginType.EMAIL)
                .language(request.getLanguage())
                .birthday(request.getBirthday())
                .build();

        // 저장
        User savedUser = userRepository.save(user);
        log.info("회원가입 성공: userId={}, email={}, language={}, birthday={}",
                savedUser.getId(), savedUser.getEmail(), savedUser.getLanguage(), savedUser.getBirthday());

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .build();
    }

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("로그인 요청: email={}", request.getEmail());

        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다"));

        // 이메일 로그인 사용자가 아닌 경우
        if (user.getLoginType() != LoginType.EMAIL) {
            throw new IllegalArgumentException("해당 이메일은 " + user.getLoginType().getDescription() + "으로 가입되었습니다");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
