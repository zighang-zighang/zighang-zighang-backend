package com.github.zighang_zighang.domain.resume.service;

import com.github.zighang_zighang.domain.resume.dto.response.ResumeResponse;
import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.domain.resume.repository.ResumeRepository;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.storage.dto.response.StorageResponse;
import com.github.zighang_zighang.global.infra.storage.uploader.NcpObjectUploader;
import com.github.zighang_zighang.global.infra.storage.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final NcpObjectUploader ncpObjectUploader;

    @Transactional
    public ResumeResponse uploadResume(User user, MultipartFile resumeFile) {
        // 2. text extractor로 뽑아서 resume 엔티티에 content도 같이 저장해야 함(hwp, pdf)

        // 확장자 + null/blank 검증
        FileValidator.validateResumeExtension(resumeFile);

        StorageResponse uploadedResume = ncpObjectUploader.uploadFile(resumeFile);

        Resume resume = Resume.builder()
                .url(uploadedResume.getFileUrl())
                .name(uploadedResume.getFileName())
                .size(uploadedResume.getSize())
                .user(user)
                .build();

        user.getResumes().add(resume);

        Resume savedResume = resumeRepository.save(resume);

        return ResumeResponse.from(savedResume);
    }

    public List<ResumeResponse> getAllResumes(User user) {

        return user.getResumes().stream()
                .map(ResumeResponse::from)
                .toList();
    }
}
