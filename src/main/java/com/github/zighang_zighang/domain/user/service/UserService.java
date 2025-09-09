package com.github.zighang_zighang.domain.user.service;

import com.github.zighang_zighang.domain.resume.entity.Resume;
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
import com.github.zighang_zighang.global.infra.storage.uploader.NcpObjectUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final NcpObjectUploader ncpObjectUploader;

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
    public UserResponse addOnboarding(User user, UserOnboardingRequest request, MultipartFile resumeFile) {

        validateOnboarding(request);

        // 관심 직군/직무, 경력, 학교, 졸업 구분, 지역 저장
        user.updateOnboardingInfo(
                request.getInterestedJobs(),
                request.getInterestedJobCategories(),
                request.getCareerYear(),
                request.getEducationLevel(),
                request.getGraduationStatus(),
                request.getPreferredRegion()
        );

        // 자기소개서 업로드
        String uploadedResumeUrl = ncpObjectUploader.uploadFile(resumeFile);

        if(uploadedResumeUrl!= null && !uploadedResumeUrl.isBlank()) {
            Resume resume = Resume.builder()
                    .resumeUrl(uploadedResumeUrl)
                    .user(user)
                    .build();

            user.getResumes().add(resume);
        }

        return UserResponse.from(user);
    }

    public void validateOnboarding(UserOnboardingRequest request) {
        List<Job> selectedJobs = request.getInterestedJobs() != null ? request.getInterestedJobs() : List.of();
        List<JobCategory> selectedCategories = request.getInterestedJobCategories() != null ? request.getInterestedJobCategories() : List.of();

        // 직군 최대 3개인지 검증
        int distinctJobCount = (int) selectedJobs.stream()
                .filter(Objects::nonNull)
                .distinct()
                .count();

        if (distinctJobCount > 3) {
            throw UserException.EXCEEDED_MAX_JOB_SELECTION.toException();
        }

        // 경력 (0-10년+) 검증
        if (request.getCareerYear() < 0 || request.getCareerYear() > 10) {
            throw UserException.INVALID_CAREER_YEAR.toException();
        }

        // 선택한 직무 중에서, 직군에 속하지 않는 항목을 필터링
        Set<Job> selectedJobSet = selectedJobs.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<JobCategory> invalidCategories = selectedCategories.stream()
                .filter(Objects::nonNull)
                .filter(category -> !selectedJobSet.contains(category.parent()))
                .toList();

        if (!invalidCategories.isEmpty()) {
            throw UserException.INVALID_JOB_CATEGORY_SELECTION.toException();
        }
    }


    public UserResponse getMyProfile(User user) {
        return UserResponse.from(user);
    }
}
