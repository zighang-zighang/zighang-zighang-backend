package com.github.zighang_zighang.domain.user.service;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.repository.UserProviderRepository;
import com.github.zighang_zighang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;

    @Transactional
    public User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        
        return userRepository.save(user);
    }

    @Transactional
    public UserProvider createUserProvider(User user, ProviderType providerType, String providerId) {
        UserProvider userProvider = UserProvider.builder()
                .user(user)
                .type(providerType)
                .providerId(providerId)
                .build();
        
        return userProviderRepository.save(userProvider);
    }
}
