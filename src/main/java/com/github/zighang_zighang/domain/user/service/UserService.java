package com.github.zighang_zighang.domain.user.service;

import com.github.zighang_zighang.domain.bookmark.repository.BookmarkRepository;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecommendedRecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.domain.resume.__keyword.service.ResumeKeywordService;
import com.github.zighang_zighang.domain.resume.service.ResumeService;
import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.repository.UserProviderRepository;
import com.github.zighang_zighang.domain.user.repository.UserRepository;
import com.github.zighang_zighang.global.infra.ai.ClovaStudioApi;
import com.github.zighang_zighang.global.infra.ai.util.ClovaCompletionRequest;
import com.github.zighang_zighang.global.infra.opensearch.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final ResumeService resumeService;
    private final OpenSearchService openSearchService;
    private final RecruitmentRepository recruitmentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ResumeKeywordService resumeKeywordService;
    private final ClovaStudioApi clovaStudioApi;

    // TODO: 프롬프트 다른 파일에 분리해서 관리 + 프롬프트 자체 개선 필요
    private static final String SYSTEM_PROMPT = """
당신은 채용 공고 추천 엔진입니다.
지원자의 이력서 키워드와 추천할 채용 공고 제목이 주어집니다.
왜 이 공고가 추천되었는지를 59자 이내의 간결한 한 문장으로 설명하세요.

조건:
- 불필요한 서두 없이, 곧바로 이유만 출력합니다.
- "~역량이 잘 맞습니다" 또는 "~적합합니다" 등으로 마무리합니다.
- 반드시 59자 이내의 문장으로 제한합니다.
- 문장이 아닌 다른 형식(X), 설명(X)

예시:
- 전략기획 및 분석 역량이 해당 직무와 잘 맞습니다
- 커뮤니케이션 강점이 고객지원 직무에 적합합니다
""";

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
        // 관심 직군/직무, 경력, 학교, 졸업 구분, 지역 저장
        user.updateOnboardingInfo(
                request.getInterestedJobs(),
                request.getInterestedJobCategories(),
                request.getCareerYear(),
                request.getEducationLevel(),
                request.getGraduationStatus(),
                request.getPreferredRegions()
        );

        // 자기소개서 업로드
        if (resumeFile != null && !resumeFile.isEmpty()) {
            resumeService.uploadResume(user, resumeFile);
        }

        return UserResponse.from(user);
    }

    public UserResponse getMyProfile(User user) {
        return UserResponse.from(user);
    }

    public List<RecommendedRecruitmentResponse> recommendJobs(User user) {
        // 1. 자소서 임베딩 조회
        float[] resumeEmbedding = openSearchService.getLatestResumeEmbedding(user);

        // 2. float[] → List<Double> 변환
        List<Double> embeddingList = new ArrayList<>();
        for (float f : resumeEmbedding) {
            embeddingList.add((double) f);
        }

        // 3. OpenSearch에서 유사한 공고 조회 (엔티티 기반)
        List<Recruitment> recruitments = recruitmentRepository.findSimilarRecruitments(embeddingList, 9);

        // 4. DTO 변환 (Bookmark 여부 체크)
        return recruitments.stream()
                .map(r -> {
                    boolean bookmarked = bookmarkRepository.existsByUserAndRecruitmentId(user, r.getId());

                    // 키워드와 공고 제목 기반 추천 이유 생성
                    List<String> keywords = resumeKeywordService.getKeywords(user).getKeywords();
                    String reason = generateRecommendationReason(keywords, r.getTitle());

                    return RecommendedRecruitmentResponse.from(r, bookmarked, reason);
                })
                .toList();
    }

    // TODO: 이유 추출 로직 개선 필요
    public String generateRecommendationReason(List<String> keywords, String title) {
        try {
            String keywordText = String.join(", ", keywords);
            String userMessage = "지원자 키워드: " + keywordText + "\n추천 공고 제목: " + title;

            String response = clovaStudioApi.completion(
                    ClovaCompletionRequest.builder()
                            .message(ClovaCompletionRequest.Message.of(ClovaCompletionRequest.Message.Role.system, SYSTEM_PROMPT))
                            .message(ClovaCompletionRequest.Message.of(ClovaCompletionRequest.Message.Role.user, userMessage))
                            .thinking(new ClovaCompletionRequest.Thinking(ClovaCompletionRequest.Thinking.Effort.none))
                            .topP(0.6F)
                            .temperature(0.5F)
                            .maxCompletionTokens(1024)
                            .includeAiFilters(false)
                            .build()
            );

            System.out.println("🔍 Clova 응답: " + response);
            return response != null ? response.trim() : "역량이 해당 공고와 적합합니다";

        } catch (Exception e) {
//            e.printStackTrace();
            return "역량이 해당 공고와 적합합니다";
        }
    }
}
