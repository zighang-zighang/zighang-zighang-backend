package com.github.zighang_zighang.global.infra.storage.service;

import com.github.zighang_zighang.global.infra.storage.dto.response.StorageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {
    StorageResponse uploadFile(MultipartFile file, UUID storageKey);
    void deleteFile(String fileName, UUID storageKey);
}
