package com.example.onkoreabe.auth;

import com.example.onkoreabe.auth.dto.AuthResponse;
import com.example.onkoreabe.auth.dto.LoginRequest;
import com.example.onkoreabe.auth.dto.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 통합 테스트
 */
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 API 성공")
    void signup_Success() throws Exception {
        // given
        SignupRequest request = new SignupRequest(
                "test@example.com",
                "password123!",
                "홍길동",
                "ko",
                LocalDate.of(1990, 1, 15)
        );

        AuthResponse response = AuthResponse.builder()
                .accessToken("jwt-token")
                .tokenType("Bearer")
                .userId(1L)
                .email("test@example.com")
                .name("홍길동")
                .build();

        given(authService.signup(any(SignupRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }

    @Test
    @DisplayName("회원가입 API 실패 - 유효성 검증 (이메일 형식)")
    void signup_Fail_InvalidEmail() throws Exception {
        // given
        SignupRequest request = new SignupRequest(
                "invalid-email",
                "password123!",
                "홍길동",
                "ko",
                LocalDate.of(1990, 1, 15)
        );

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 API 실패 - 유효성 검증 (비밀번호 짧음)")
    void signup_Fail_ShortPassword() throws Exception {
        // given
        SignupRequest request = new SignupRequest(
                "test@example.com",
                "short",
                "홍길동",
                "ko",
                LocalDate.of(1990, 1, 15)
        );

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 API 성공")
    void login_Success() throws Exception {
        // given
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "password123!"
        );

        AuthResponse response = AuthResponse.builder()
                .accessToken("jwt-token")
                .tokenType("Bearer")
                .userId(1L)
                .email("test@example.com")
                .name("홍길동")
                .build();

        given(authService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }

    @Test
    @DisplayName("로그인 API 실패 - 유효성 검증 (필수 값 누락)")
    void login_Fail_MissingFields() throws Exception {
        // given
        String invalidJson = "{\"email\": \"test@example.com\"}"; // password 누락

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
