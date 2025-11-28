package com.example.onkoreabe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                        대부분의 API는 OAuth2 인증이 필요합니다.
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
                        .url("http://localhost:8080")
                        .description("로컬 개발 서버"),
                new Server()
                        .url("https://api.onkorea.com")
                        .description("운영 서버")
        );
    }

    /**
     * 보안 스킴 설정 (OAuth2)
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("oauth2", new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .description("Google OAuth2 인증")
                        .name("oauth2"));
    }

    /**
     * 보안 요구사항
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("oauth2");
    }
}
