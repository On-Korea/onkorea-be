package com.example.onkoreabe.user;

import com.example.onkoreabe.user.dto.ProfileUpdateRequest;
import com.example.onkoreabe.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * UserService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .role(Role.USER)
                .loginType(LoginType.EMAIL)
                .language("ko")
                .birthday(LocalDate.of(1990, 1, 15))
                .build();
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void getUserByEmail_Success() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUserByEmail("test@example.com");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getLanguage()).isEqualTo("ko");
        assertThat(response.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 15));

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 실패 - 사용자 없음")
    void getUserByEmail_Fail_UserNotFound() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserByEmail("notfound@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_Success() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "en",
                LocalDate.of(1995, 5, 20)
        );

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.updateProfile("test@example.com", request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getLanguage()).isEqualTo("en");
        assertThat(response.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 20));

        verify(userRepository).findByEmail("test@example.com");
    }
}
