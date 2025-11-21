package com.example.onkoreabe.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원가입 요청 DTO
 */
@Schema(description = "회원가입 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Schema(description = "이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;

    @Schema(description = "비밀번호 (최소 8자)", example = "password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하이어야 합니다")
    private String name;

    @Schema(description = "언어 설정 (ko, en 등)", example = "ko", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "언어 설정은 필수입니다")
    private String language;

    @Schema(
            description = "생년월일",
            example = "1990-01-15",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            format = "date"
    )
    @NotNull(message = "생년월일은 필수입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
