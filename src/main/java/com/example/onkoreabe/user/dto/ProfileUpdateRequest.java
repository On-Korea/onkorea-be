package com.example.onkoreabe.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 사용자 프로필 업데이트 요청 DTO
 */
@Schema(description = "프로필 업데이트 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Schema(
            description = "사용자 언어 설정 (ko, en 등)",
            example = "ko",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "언어 설정은 필수입니다")
    private String language;

    @Schema(
            description = "사용자 생년월일",
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
