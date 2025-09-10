package com.github.zighang_zighang.domain.resume.controller;

import com.github.zighang_zighang.domain.resume.dto.response.ResumeResponse;
import com.github.zighang_zighang.domain.resume.service.ResumeService;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.auth.resolver.CurrentUser;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // TODO: Resume 파일 업로드(+text extractor), 파일 리스트 가져오기, 파일 삭제하기, 파일 업로드 시 인코딩해서 url 생성

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ResumeResponse> uploadResume(
            @CurrentUser User user,
            @RequestPart(value = "resumeFile") MultipartFile resumeFile
    ) {
        return ApiResponse.ok(resumeService.uploadResume(user, resumeFile));
    }
}
