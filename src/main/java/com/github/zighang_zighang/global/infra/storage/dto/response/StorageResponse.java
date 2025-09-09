package com.github.zighang_zighang.global.infra.storage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class StorageResponse {

    @Schema(description = "업로드된 파일의 ncp 스토리지 url")
    String fileUrl;

    public static StorageResponse from(String fileUrl) {

        return StorageResponse.of(
                fileUrl
        );
    }

}
