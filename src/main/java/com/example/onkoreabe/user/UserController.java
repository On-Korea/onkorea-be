package com.example.onkoreabe.user;

import com.example.onkoreabe.user.dto.ProfileUpdateRequest;
import com.example.onkoreabe.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API 컨트롤러
 */
@Tag(name = "User", description = "사용자 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자 정보 조회
     *
     * @param principal OAuth2 인증 정보
     * @return 사용자 정보
     */
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        String email = principal.getAttribute("email");
        log.info("사용자 정보 조회 요청: email={}", email);

        UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * 사용자 프로필 업데이트 (language, birthday)
     *
     * @param principal OAuth2 인증 정보
     * @param request   프로필 업데이트 요청
     * @return 업데이트된 사용자 정보
     */
    @Operation(summary = "내 프로필 수정", description = "현재 로그인한 사용자의 언어 설정과 생일 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/me/profile")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        String email = principal.getAttribute("email");
        log.info("프로필 업데이트 요청: email={}, language={}, birthday={}",
                email, request.getLanguage(), request.getBirthday());

        UserResponse userResponse = userService.updateProfile(email, request);
        return ResponseEntity.ok(userResponse);
    }
}
