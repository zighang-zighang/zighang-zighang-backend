package com.github.zighang_zighang.global.infra.storage.util;

import com.github.zighang_zighang.global.infra.storage.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidator {

    private static final List<String> RESUME_ALLOWED_EXTENSIONS = List.of("pdf", "hwp");

    public static void validateResumeExtension(MultipartFile file) {
        if (file == null) {
            throw StorageException.FILE_REQUIRED.toException();
        }
        if (file.isEmpty()) {
            throw StorageException.EMPTY_FILE.toException();
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw StorageException.UNKNOWN_FILE_EXTENSION.toException();
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        if (!RESUME_ALLOWED_EXTENSIONS.contains(extension)) {
            throw StorageException.INVALID_FILE_EXTENSION.toException();
        }
    }

}
