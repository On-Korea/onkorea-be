package com.example.onkoreabe.config;

import com.example.onkoreabe.auth.CustomOAuth2UserService;
import com.example.onkoreabe.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API 서버로 사용 시)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                // 세션 정책: JWT 사용하므로 STATELESS 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authz -> authz
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()

                        // 회원가입, 로그인 API는 인증 불필요
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Swagger UI 및 API Docs는 인증 불필요
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger.html").permitAll()

                        // OAuth2 로그인 관련 경로
                        .requestMatchers("/oauth2/**", "/login/**", "/loginsuccess").permitAll()

                        // 그 외 모든 API는 인증 필요
                        .requestMatchers("/api/v1/**").authenticated()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 배치)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 리다이렉트될 주소
                )

                // OAuth2 로그인 (Google 등)
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 성공 후 리다이렉트 될 주소
                        .defaultSuccessUrl("/loginsuccess")
                        // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                        .userInfoEndpoint(userInfo -> userInfo
                                // 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체 등록
                                .userService(customOAuth2UserService)
                        )
                );

        return http.build();
    }
}
