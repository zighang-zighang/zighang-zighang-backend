package com.github.zighang_zighang.domain.resume.__keyword.controller;

import com.github.zighang_zighang.domain.resume.__keyword.api.ResumeKeywordApi;
import com.github.zighang_zighang.domain.resume.__keyword.dto.response.ResumeKeywordResponse;
import com.github.zighang_zighang.domain.resume.__keyword.service.ResumeKeywordService;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.auth.resolver.CurrentUser;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resumes/keywords")
@RequiredArgsConstructor
public class ResumeKeywordController implements ResumeKeywordApi {

    private final ResumeKeywordService resumeKeywordService;

    @Override
    @GetMapping
    public ApiResponse<ResumeKeywordResponse> getKeywords(@CurrentUser User user) {

        return ApiResponse.ok(resumeKeywordService.getKeywords(user));
    }
}
