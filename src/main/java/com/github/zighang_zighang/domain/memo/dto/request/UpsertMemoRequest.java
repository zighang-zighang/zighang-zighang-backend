package com.github.zighang_zighang.domain.memo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertMemoRequest {

    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private String title;

    @Size(max = 5000, message = "메모는 5000자를 초과할 수 없습니다.")
    private String content;
}