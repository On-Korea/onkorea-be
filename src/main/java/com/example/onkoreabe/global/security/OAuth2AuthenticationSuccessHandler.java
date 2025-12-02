package com.example.onkoreabe.global.security;

import com.example.onkoreabe.auth.jwt.JwtTokenProvider;
import com.example.onkoreabe.user.User;
import com.example.onkoreabe.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 발급하고 iOS 앱으로 리다이렉트하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        log.info("OAuth2 로그인 성공: email={}", email);

        // 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + email));

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getId());

        log.info("JWT 토큰 발급 완료: userId={}, email={}", user.getId(), email);

        // iOS 앱으로 리다이렉트 (Custom URL Scheme)
        String targetUrl = UriComponentsBuilder.fromUriString("onkorea://callback")
                .queryParam("token", accessToken)
                .queryParam("email", email)
                .queryParam("name", user.getName())
                .build()
                .toUriString();

        log.info("iOS 앱으로 리다이렉트: {}", targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
