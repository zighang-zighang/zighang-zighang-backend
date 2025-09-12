package com.github.zighang_zighang.domain.resume.__keyword.service;

import com.github.zighang_zighang.domain.resume.__keyword.dto.response.ResumeKeywordResponse;
import com.github.zighang_zighang.domain.resume.__keyword.entity.ResumeKeyword;
import com.github.zighang_zighang.domain.resume.__keyword.repository.ResumeKeywordRepository;
import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.domain.resume.repository.ResumeRepository;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.ai.ClovaStudioApi;
import com.github.zighang_zighang.global.infra.ai.util.ClovaCompletionRequest;
import com.github.zighang_zighang.global.infra.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeKeywordService {

    private final ResumeRepository resumeRepository;
    private final ResumeKeywordRepository resumeKeywordRepository;

    private final ClovaStudioApi clovaStudioApi;
    private final StorageService storageService;

    public ResumeKeywordResponse getKeywords(User user) {

        return ResumeKeywordResponse.from(resumeKeywordRepository.findByUser(user));
    }

    public void updateKeywords(User user) {

        String content = resumeRepository.findByUser(user).stream()
                .map(Resume::getContent)
                .collect(Collectors.joining("\n\n"));

        String completion = clovaStudioApi.completion(
                ClovaCompletionRequest.builder()
                        .message(ClovaCompletionRequest.Message.of(ClovaCompletionRequest.Message.Role.system, SYSTEM_PROMPT))
                        .message(ClovaCompletionRequest.Message.of(ClovaCompletionRequest.Message.Role.user, content))
                        .thinking(new ClovaCompletionRequest.Thinking(ClovaCompletionRequest.Thinking.Effort.none))
                        .topP(0.6F)
                        .temperature(0.5F)
                        .maxCompletionTokens(1024)
                        .includeAiFilters(false)
                        .build()
        );

        resumeKeywordRepository.deleteAllByUser(user);

        resumeKeywordRepository.saveAll(
                Arrays.stream(completion.split(",\\s*"))
                        .map(keyword -> ResumeKeyword.builder().user(user).keyword(keyword).build())
                        .toList()
        );
    }

    private static final String SYSTEM_PROMPT = """
            당신은 이력서에서 지원자의 전체적인 분위기, 직무적 특성, 성향, 그리고 업무 스타일을 파악하여 핵심 키워드를 추출하는 전문가입니다.
            다음 조건을 엄격히 준수하여 주어진 이력서 내용을 분석해 가장 중요한 **5개의 키워드**를 추출하세요.
            
            1. 키워드는 지원자의 전문 분야, 업무 방식, 성과 유형, 성격적 강점, 협업 스타일 등 **직무와 관련된 전반적인 분위기와 특성**을 나타내는 명사 형태여야 합니다.
            2. 기술 스택, 특정 도구, 프로그래밍 언어 등 구체적인 기술 키워드는 제외하고, 지원자의 직장 내 역할과 동작 방식, 핵심 강점에 집중하세요.
            3. 중복 없이, 간결하고 명확한 단어만 사용하며, 구성은 반드시 명사형으로 제한합니다.
            4. 출력은 다음 형식을 반드시 준수합니다:
            - 키워드는 쉼표(,)로 구분합니다.
            - 반드시 한 줄 문자열로 출력하며, 불필요한 설명이나 다른 텍스트는 포함하지 않습니다.
            - 띄어쓰기는 키워드와 쉼표 사이에 한 칸씩 넣습니다.
            
            예시 출력:
            문제해결능력, 팀워크, 전략기획, 고객중심, 데이터분석
            
            이 조건을 바탕으로 이력서 본문에서 핵심적인 5개의 키워드를 추출해주세요.""";
}
