package com.github.zighang_zighang.domain.resume.controller;

import com.github.zighang_zighang.domain.resume.api.ResumeApi;
import com.github.zighang_zighang.domain.resume.dto.response.ResumeResponse;
import com.github.zighang_zighang.domain.resume.service.ResumeService;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.auth.resolver.CurrentUser;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController implements ResumeApi {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ResumeResponse> uploadResume(
            @CurrentUser User user,
            @RequestPart(value = "resumeFile") MultipartFile resumeFile
    ) {
        return ApiResponse.ok(resumeService.uploadResume(user, resumeFile));
    }

    @GetMapping
    public ApiResponse<List<ResumeResponse>> getAllResumes(
            @CurrentUser User user
    ) {
        return ApiResponse.ok(resumeService.getAllResumes(user));
    }

    @DeleteMapping("/{resumeId}")
    public ApiResponse<Void> deleteResume(
            @CurrentUser User user,
            @PathVariable UUID resumeId
    ) {
        resumeService.deleteResume(user, resumeId);
        return ApiResponse.ok(null);
    }
}
