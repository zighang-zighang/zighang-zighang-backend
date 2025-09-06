package com.github.zighang_zighang.domain.memo.dto.response;

import com.github.zighang_zighang.domain.memo.entity.Memo;
import com.github.zighang_zighang.domain.recruitment.service.RecruitmentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemosResponse {

    private List<MemoResponse> memos;

    public static MemosResponse from(List<Memo> memos, RecruitmentService service) {

        return MemosResponse.builder()
                .memos(memos.stream().map(memo -> MemoResponse.from(memo, service)).toList())
                .build();
    }
}