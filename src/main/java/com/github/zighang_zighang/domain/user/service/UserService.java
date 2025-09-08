package com.github.zighang_zighang.domain.user.service;

import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.domain.resume.repository.ResumeRepository;
import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.exception.UserException;
import com.github.zighang_zighang.domain.user.repository.UserProviderRepository;
import com.github.zighang_zighang.domain.user.repository.UserRepository;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final ResumeRepository resumeRepository;

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

        validateOnboarding(request);

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ApiException(UserException.NOT_FOUND));

        // TODO: 자기소개서 업로드

        // 관심 직군/직무, 경력, 학교, 졸업 구분, 지역 저장
        managedUser.updateOnboardingInfo(
                request.getInterestedJobs(),
                request.getInterestedJobCategories(),
                request.getCareerYears(),
                request.getEducationLevel(),
                request.getGraduationStatus(),
                request.getPreferredRegion()
        );

        if(request.getResumeUrl() != null && !request.getResumeUrl().isBlank()) {
            Resume resume = Resume.builder()
                    .resumeUrl(request.getResumeUrl())
                    .user(managedUser)
                    .build();

            managedUser.getResumes().add(resume);
        }

        return UserResponse.from(managedUser);
    }

    public void validateOnboarding(UserOnboardingRequest request) {
        List<Job> selectedJobs = request.getInterestedJobs();
        List<JobCategory> selectedCategories = request.getInterestedJobCategories();

        // 직군 최대 3개인지 검증
        if (selectedJobs.size() > 3) {
            throw new ApiException(UserException.EXCEEDED_MAX_JOB_SELECTION);
        }

        // 선택한 직무 중에서, 직군에 속하지 않는 항목을 필터링
        List<JobCategory> invalidCategories = selectedCategories.stream()
                .filter(category -> !selectedJobs.contains(category.parent()))
                .toList();

        if (!invalidCategories.isEmpty()) {
            throw new ApiException(UserException.INVALID_JOB_CATEGORY_SELECTION);
        }
    }


    public UserResponse getMyProfile(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ApiException(UserException.NOT_FOUND));

        return UserResponse.from(managedUser);
    }
}
