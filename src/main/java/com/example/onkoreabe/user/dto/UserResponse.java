package com.example.onkoreabe.user.dto;

import com.example.onkoreabe.user.LoginType;
import com.example.onkoreabe.user.Role;
import com.example.onkoreabe.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 사용자 정보 응답 DTO
 */
@Schema(description = "사용자 정보 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "사용자 권한", example = "USER")
    private Role role;

    @Schema(description = "사용자 언어 설정", example = "ko")
    private String language;

    @Schema(description = "사용자 생년월일", example = "1990-01-15", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Schema(description = "로그인 타입", example = "GOOGLE")
    private LoginType loginType;

    /**
     * User 엔티티로부터 UserResponse 생성
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .language(user.getLanguage())
                .birthday(user.getBirthday())
                .loginType(user.getLoginType())
                .build();
    }
}
