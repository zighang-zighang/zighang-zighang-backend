package com.github.zighang_zighang.domain.user.service;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.exception.UserException;
import com.github.zighang_zighang.domain.user.repository.UserProviderRepository;
import com.github.zighang_zighang.domain.user.repository.UserRepository;
import com.github.zighang_zighang.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UserResponse addOnboarding(User user, UserOnboardingRequest request) {

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ApiException(UserException.NOT_FOUND));

        // TODO: 직무가 직군에 해당하는지 검증
        // TODO: 자기소개서 업로드

        // 관심 직군/직무, 경력, 학력, 지역 저장
        managedUser.updateOnboardingInfo(
                request.getInterestedJobs(),
                request.getInterestedJobCategories(),
                request.getCareerYears(),
                request.getEducationLevel(),
                request.getPreferredRegion()
        );

        return UserResponse.from(managedUser);
    }

}
