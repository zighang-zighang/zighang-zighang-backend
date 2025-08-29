package com.github.zighang_zighang.global.auth.service;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.repository.UserProviderRepository;
import com.github.zighang_zighang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User o = super.loadUser(req);
        Map<String, Object> a = o.getAttributes();

        String providerId = String.valueOf(a.get("sub"));   // 구글 고유 id
        String email = (String) a.get("email");
        String name = (String) a.getOrDefault("name", email);

        // provider로 바로 매칭
        UserProvider link = userProviderRepository
                .findByProviderIdAndType(providerId, ProviderType.GOOGLE)
                .orElse(null);

        User user;
        if (link != null) {
            user = link.getUser();
        } else {
            user = userRepository.findByEmail(email).orElseGet(() ->
                    userRepository.save(User.builder()
                            .email(email)
                            .name(name)
                            .build())
            );
            userProviderRepository.save(
                    UserProvider.builder()
                            .user(user)
                            .type(ProviderType.GOOGLE)
                            .providerId(providerId)
                            .build()
            );
        }

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                a,
                "sub"   // principal name key
        );
    }
}
