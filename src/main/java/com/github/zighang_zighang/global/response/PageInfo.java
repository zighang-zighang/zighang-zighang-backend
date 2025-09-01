package com.github.zighang_zighang.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PageInfo {

    @Schema(description = "페이지 크기", example = "20")
    Integer size;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    Integer page;

    @Schema(description = "전체 요소 수", example = "10000")
    Integer totalElements;

    @Schema(description = "전체 페이지 수", example = "500")
    Integer totalPage;

    public static PageInfo of(int size, int page, long totalElements) {

        return new PageInfo(size, page, (int) totalElements, (int) Math.ceil((double) totalElements / size));
    }
}