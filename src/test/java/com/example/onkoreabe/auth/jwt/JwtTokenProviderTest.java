package com.example.onkoreabe.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtTokenProvider 단위 테스트
 */
@DisplayName("JwtTokenProvider 테스트")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long-for-security";
        long expiration = 3600000; // 1시간
        jwtTokenProvider = new JwtTokenProvider(secret, expiration);
    }

    @Test
    @DisplayName("JWT 토큰 생성 성공")
    void generateToken_Success() {
        // given
        String email = "test@example.com";
        Long userId = 1L;

        // when
        String token = jwtTokenProvider.generateToken(email, userId);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("JWT 토큰에서 이메일 추출 성공")
    void getEmailFromToken_Success() {
        // given
        String email = "test@example.com";
        Long userId = 1L;
        String token = jwtTokenProvider.generateToken(email, userId);

        // when
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        // then
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("JWT 토큰에서 사용자 ID 추출 성공")
    void getUserIdFromToken_Success() {
        // given
        String email = "test@example.com";
        Long userId = 1L;
        String token = jwtTokenProvider.generateToken(email, userId);

        // when
        Long extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("JWT 토큰 유효성 검증 성공")
    void validateToken_Success() {
        // given
        String email = "test@example.com";
        Long userId = 1L;
        String token = jwtTokenProvider.generateToken(email, userId);

        // when
        boolean isValid = jwtTokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("JWT 토큰 유효성 검증 실패 - 잘못된 토큰")
    void validateToken_Fail_InvalidToken() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("JWT 토큰 유효성 검증 실패 - 빈 토큰")
    void validateToken_Fail_EmptyToken() {
        // given
        String emptyToken = "";

        // when
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // then
        assertThat(isValid).isFalse();
    }
}
