package com.github.zighang_zighang.domain.recruitment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "페이지네이션 응답")
public class PageResponse<T> {

    @Schema(description = "데이터 목록")
    private List<T> content;

    @Schema(description = "페이지 정보")
    private PageInfo page;

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                new PageInfo(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "페이지 정보")
    public static class PageInfo {
        @Schema(description = "페이지 크기", example = "20")
        private int size;

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        private int number;

        @Schema(description = "전체 요소 수", example = "17598")
        private long totalElements;

        @Schema(description = "전체 페이지 수", example = "880")
        private int totalPages;
    }
}