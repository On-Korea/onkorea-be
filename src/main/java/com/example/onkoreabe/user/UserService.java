package com.example.onkoreabe.user;

import com.example.onkoreabe.user.dto.ProfileUpdateRequest;
import com.example.onkoreabe.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 이메일로 사용자 조회
     *
     * @param email 사용자 이메일
     * @return 사용자 정보
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        return UserResponse.from(user);
    }

    /**
     * 사용자 프로필 업데이트 (language, birthday)
     *
     * @param email   사용자 이메일
     * @param request 프로필 업데이트 요청
     * @return 업데이트된 사용자 정보
     */
    @Transactional
    public UserResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        user.updateProfile(request.getLanguage(), request.getBirthday());
        User updatedUser = userRepository.save(user);

        log.info("사용자 프로필 업데이트 완료: email={}, language={}, birthday={}",
                email, request.getLanguage(), request.getBirthday());

        return UserResponse.from(updatedUser);
    }
}
