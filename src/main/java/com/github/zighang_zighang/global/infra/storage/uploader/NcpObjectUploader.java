package com.github.zighang_zighang.global.infra.storage.uploader;

import com.github.zighang_zighang.global.infra.storage.dto.response.StorageResponse;
import com.github.zighang_zighang.global.infra.storage.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcpObjectUploader {

    private final S3Client s3Client;

    @Value("${ncp.storage.bucket-name}")
    private String bucketName;

    @Value("${ncp.storage.endpoint}")
    private String endpoint;

    public StorageResponse uploadFile(MultipartFile file, UUID storageKey) {
        try {
            String originalFilename = file.getOriginalFilename() != null
                    ? file.getOriginalFilename() : "unnamed-file";
            String uniqueFileName = storageKey.toString() + "_" + originalFilename;

            // S3 업로드 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            // 파일 업로드 실행
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 업로드된 파일의 URL 반환
            String encodedFileName = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            String uploadedFileUrl = endpoint + "/" + bucketName + "/" + encodedFileName;

            return StorageResponse.from(
                    originalFilename,
                    uploadedFileUrl,
                    file.getSize()
            );
        } catch (IOException e) {
            throw StorageException.UPLOAD_FAILED.toException();
        }
    }

}
