package com.github.zighang_zighang.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.function.Function;

@Schema
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class PageResponse<T> {

    @Schema(description = "데이터 목록")
    List<T> content;

    @Schema(description = "페이지 정보")
    PageInfo page;

    public <NEW> PageResponse<NEW> map(Function<? super T, NEW> converter) {

        return PageResponse.of(this.content.stream().map(converter).toList(), this.page);
    }
}