package com.github.zighang_zighang.domain.resume.service;

import com.github.zighang_zighang.domain.resume.dto.response.ResumeResponse;
import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.domain.resume.exception.ResumeException;
import com.github.zighang_zighang.domain.resume.repository.ResumeRepository;
import com.github.zighang_zighang.domain.resume.util.ResumeTextExtractor;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.storage.dto.response.StorageResponse;
import com.github.zighang_zighang.global.infra.storage.service.StorageService;
import com.github.zighang_zighang.global.infra.storage.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final StorageService storageService;
    private final ResumeTextExtractor textExtractor;

    @Transactional
    public ResumeResponse uploadResume(User user, MultipartFile resumeFile) {
        FileValidator.validateResumeExtension(resumeFile);

        UUID resumeKey = UUID.randomUUID();
        StorageResponse uploadedResume = storageService.uploadFile(resumeFile, resumeKey);

        // 텍스트 추출
        String extractedContent = textExtractor.extractText(resumeFile);

        Resume resume = Resume.builder()
                .url(uploadedResume.getFileUrl())
                .name(uploadedResume.getFileName())
                .size(uploadedResume.getSize())
                .storageKey(resumeKey)
                .user(user)
                .content(extractedContent)
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

    @Transactional
    public void deleteResume(User user, UUID resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(ResumeException.NOT_FOUND::toException);

        if (!resume.getUser().equals(user)) {
            throw ResumeException.FORBIDDEN.toException();
        }

        storageService.deleteFile(resume.getName(), resume.getStorageKey());

        resumeRepository.delete(resume);
    }
}
