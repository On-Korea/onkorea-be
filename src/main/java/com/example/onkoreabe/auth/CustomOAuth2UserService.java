package com.example.onkoreabe.auth;

import com.example.onkoreabe.user.LoginType;
import com.example.onkoreabe.user.Role;
import com.example.onkoreabe.user.User;
import com.example.onkoreabe.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth2 공급자 (google, kakao, naver 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 시 키가 되는 필드 값 (Primary Key와 같은 역할)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        // registrationId를 LoginType enum으로 변환
        LoginType loginType = convertToLoginType(registrationId);

        User user = saveOrUpdate(email, name, loginType, userNameAttributeName);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes,
                userNameAttributeName);
    }

    /**
     * registrationId를 LoginType enum으로 변환
     */
    private LoginType convertToLoginType(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> LoginType.GOOGLE;
            default -> {
                log.warn("알 수 없는 OAuth2 제공자: {}", registrationId);
                yield LoginType.GOOGLE; // 기본값
            }
        };
    }

    /**
     * DB에 사용자 정보가 없으면 새로 저장하고, 있으면 업데이트
     */
    private User saveOrUpdate(String email, String name, LoginType loginType, String providerId) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            // 이미 가입된 유저면 정보 업데이트
            user = userOptional.get();
            user.update(name);
            log.info("기존 사용자 정보 업데이트: email={}, loginType={}", email, loginType);
        } else {
            // 첫 소셜 로그인이면 신규 가입
            user = User.builder()
                    .name(name)
                    .email(email)
                    .role(Role.USER)
                    .loginType(loginType)
                    .providerId(providerId)
                    .build();
            log.info("신규 사용자 가입: email={}, loginType={}", email, loginType);
        }
        return userRepository.save(user);
    }
}
