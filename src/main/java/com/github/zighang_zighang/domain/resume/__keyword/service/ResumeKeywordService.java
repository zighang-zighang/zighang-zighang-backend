package com.github.zighang_zighang.domain.resume.__keyword.service;

import com.github.zighang_zighang.domain.resume.__keyword.dto.response.ResumeKeywordResponse;
import com.github.zighang_zighang.domain.resume.__keyword.entity.ResumeKeyword;
import com.github.zighang_zighang.domain.resume.__keyword.repository.ResumeKeywordRepository;
import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.domain.resume.repository.ResumeRepository;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.ai.ClovaStudioApi;
import com.github.zighang_zighang.global.infra.ai.util.ClovaCompletionRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeKeywordService {

    private final ResumeRepository resumeRepository;
    private final ResumeKeywordRepository resumeKeywordRepository;

    private final ClovaStudioApi clovaStudioApi;

    private static final String SYSTEM_PROMPT = loadPrompt();

    @SneakyThrows(IOException.class)
    private static String loadPrompt() {

        ClassPathResource resource = new ClassPathResource("prompt/keyword_extract.txt");
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }

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
}
