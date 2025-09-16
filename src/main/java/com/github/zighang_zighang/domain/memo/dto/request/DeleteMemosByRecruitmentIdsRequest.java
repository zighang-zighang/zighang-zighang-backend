package com.github.zighang_zighang.domain.memo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMemosByRecruitmentIdsRequest {

    @NotEmpty(message = "삭제할 공고 ID 목록은 비어 있을 수 없습니다.")
    List<UUID> recruitments;
}