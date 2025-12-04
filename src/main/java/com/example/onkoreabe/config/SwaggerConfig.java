package com.example.onkoreabe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:onkorea-be}")
    private String applicationName;

    @Value("${SERVER_URL:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .components(securityComponents())
                .addSecurityItem(securityRequirement());
    }

    /**
     * API 기본 정보
     */
    private Info apiInfo() {
        return new Info()
                .title("OnKorea API")
                .description("""
                        OnKorea 백엔드 API 문서입니다.

                        ## 주요 기능
                        - Google OAuth2 소셜 로그인
                        - 사용자 프로필 관리
                        - AI 챗봇 서비스

                        ## 인증 방법

                        ### 1. JWT Bearer Token (권장)
                        - `/api/v1/auth/login`으로 로그인하여 JWT 토큰 받기
                        - 우측 상단 "Authorize" 버튼 → "bearerAuth" 선택
                        - 받은 토큰을 입력하여 인증

                        ### 2. OAuth2 (Google)
                        - 브라우저에서 `/oauth2/authorization/google` 접속
                        - Google 로그인 후 JWT 토큰 자동 발급
                        - iOS 앱으로 토큰 전달 (onkorea://callback)

                        **주의**: Swagger UI에서는 JWT Bearer Token 방식만 테스트 가능합니다.
                        OAuth2는 브라우저를 통해 직접 테스트하세요.
                        """)
                .version("v1.0.0")
                .contact(new Contact()
                        .name("OnKorea Team")
                        .email("contact@onkorea.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * 서버 목록
     */
    private List<Server> servers() {
        return List.of(
                new Server()
                        .url(serverUrl)
                        .description("API Server")
        );
    }

    /**
     * 보안 스킴 설정 (JWT Bearer + OAuth2)
     */
    private Components securityComponents() {
        return new Components()
                // JWT Bearer Token 인증
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .description("JWT 토큰을 사용한 인증. 로그인 API(/api/v1/auth/login)를 통해 토큰을 받아 사용하세요.")
                )
                // OAuth2 인증 (문서화 목적)
                .addSecuritySchemes("oauth2", new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .description("Google OAuth2 소셜 로그인. 브라우저에서 /oauth2/authorization/google로 접속하여 인증하세요.")
                        .flows(new OAuthFlows()
                                .authorizationCode(new OAuthFlow()
                                        .authorizationUrl(serverUrl + "/oauth2/authorization/google")
                                        .tokenUrl(serverUrl + "/login/oauth2/code/google")
                                        .scopes(new Scopes()
                                                .addString("profile", "사용자 프로필 정보")
                                                .addString("email", "사용자 이메일")
                                        )
                                )
                        )
                );
    }

    /**
     * 보안 요구사항 (JWT 또는 OAuth2)
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement()
                .addList("bearerAuth")    // JWT 인증
                .addList("oauth2");       // 또는 OAuth2 인증
    }
}
