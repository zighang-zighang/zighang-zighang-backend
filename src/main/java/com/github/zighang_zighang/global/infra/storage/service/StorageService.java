package com.github.zighang_zighang.global.infra.storage.service;

import com.github.zighang_zighang.global.infra.storage.dto.response.StorageResponse;
import com.github.zighang_zighang.global.infra.storage.uploader.NcpObjectUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorageService {

    private final NcpObjectUploader ncpObjectUploader;

    public StorageResponse uploadFile(MultipartFile file) {

        return ncpObjectUploader.uploadFile(file);
    }

}
