package com.example.onkoreabe.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 방식을 나타내는 Enum
 */
@Getter
@RequiredArgsConstructor
public enum LoginType {

    /**
     * 일반 이메일/비밀번호 로그인
     */
    EMAIL("이메일 로그인"),

    /**
     * Google OAuth2 소셜 로그인
     */
    GOOGLE("구글 로그인");

    private final String description;
}
