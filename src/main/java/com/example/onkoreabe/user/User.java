package com.example.onkoreabe.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 나중에 회원가입 시 사용자가 입력 (nullable)
    @Column(nullable = true)
    private String language;

    // 나중에 회원가입 시 사용자가 입력 (nullable)
    @Column(nullable = true)
    private LocalDate birthday;

    // 로그인 방식 (EMAIL, GOOGLE, APPLE, KAKAO, NAVER 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    // 소셜 로그인 제공자의 고유 ID (일반 로그인은 null)
    @Column(nullable = true, unique = true)
    private String providerId;

    @Builder
    public User(String email, String password, String name, Role role, String language,
                LocalDate birthday, LoginType loginType, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.language = language;
        this.birthday = birthday;
        this.loginType = loginType;
        this.providerId = providerId;
    }

    /**
     * 사용자 정보 업데이트
     */
    public User update(String name) {
        this.name = name;
        return this;
    }

    /**
     * Spring Security의 권한을 위한 Role Key 반환
     */
    public String getRoleKey() {
        return this.role.getKey();
    }

    /**
     * 회원가입 완료 시 추가 정보 업데이트
     */
    public void updateProfile(String language, LocalDate birthday) {
        this.language = language;
        this.birthday = birthday;
    }
}
