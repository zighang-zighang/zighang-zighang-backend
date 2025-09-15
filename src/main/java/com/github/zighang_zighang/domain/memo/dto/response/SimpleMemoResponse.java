package com.github.zighang_zighang.domain.memo.dto.response;

import com.github.zighang_zighang.domain.memo.entity.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMemoResponse {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String title;
    private String content;

    public static SimpleMemoResponse from(Memo memo) {

        return SimpleMemoResponse.builder()
                .id(memo.getId())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .title(memo.getTitle())
                .content(memo.getContent())
                .build();
    }
}