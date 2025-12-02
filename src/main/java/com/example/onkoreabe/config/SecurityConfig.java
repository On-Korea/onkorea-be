package com.example.onkoreabe.config;

import com.example.onkoreabe.auth.CustomOAuth2UserService;
import com.example.onkoreabe.auth.jwt.JwtAuthenticationFilter;
import com.example.onkoreabe.global.security.JwtAccessDeniedHandler;
import com.example.onkoreabe.global.security.JwtAuthenticationEntryPoint;
import com.example.onkoreabe.global.security.OAuth2AuthenticationFailureHandler;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String[] allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API 서버로 사용 시)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
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

                // 예외 처리 설정: 인증/인가 실패 시 JSON 응답
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // JWT 인증 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 배치)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 리다이렉트될 주소
                )

                // OAuth2 로그인 (Google 등)
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/loginsuccess")
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        return http.build();
    }

    /**
     * CORS 설정
     * 프론트엔드가 다른 도메인/포트에서 API를 호출할 수 있도록 허용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
