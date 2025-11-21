package com.example.onkoreabe.auth;

import com.example.onkoreabe.auth.dto.AuthResponse;
import com.example.onkoreabe.auth.dto.LoginRequest;
import com.example.onkoreabe.auth.dto.SignupRequest;
import com.example.onkoreabe.auth.jwt.JwtTokenProvider;
import com.example.onkoreabe.user.LoginType;
import com.example.onkoreabe.user.Role;
import com.example.onkoreabe.user.User;
import com.example.onkoreabe.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * AuthService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest(
                "test@example.com",
                "password123!",
                "홍길동",
                "ko",
                LocalDate.of(1990, 1, 15)
        );

        loginRequest = new LoginRequest(
                "test@example.com",
                "password123!"
        );

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("홍길동")
                .role(Role.USER)
                .loginType(LoginType.EMAIL)
                .language("ko")
                .birthday(LocalDate.of(1990, 1, 15))
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);
        given(jwtTokenProvider.generateToken(anyString(), any(Long.class)))
                .willReturn("jwt-token");

        // when
        AuthResponse response = authService.signup(signupRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getUserId()).isEqualTo(1L);

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("password123!");
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken("test@example.com", 1L);
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_Fail_DuplicateEmail() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 이메일입니다");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.generateToken(anyString(), any(Long.class)))
                .willReturn("jwt-token");

        // when
        AuthResponse response = authService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("홍길동");

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123!", "encodedPassword");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_Fail_EmailNotFound() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_WrongPassword() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    @DisplayName("로그인 실패 - OAuth2 사용자")
    void login_Fail_OAuth2User() {
        // given
        User googleUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .role(Role.USER)
                .loginType(LoginType.GOOGLE)
                .build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(googleUser));

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 이메일은 구글 로그인으로 가입되었습니다");
    }
}
