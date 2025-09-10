package com.github.zighang_zighang.global.infra.storage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class StorageResponse {

    @Schema(description = "파일명")
    String fileName;

    @Schema(description = "업로드된 파일의 ncp 스토리지 url")
    String fileUrl;

    @Schema(description = "파일 크기")
    long size;

    public static StorageResponse from(String fileName, String fileUrl, long size) {

        return StorageResponse.of(
                fileName,
                fileUrl,
                size
        );
    }

}
