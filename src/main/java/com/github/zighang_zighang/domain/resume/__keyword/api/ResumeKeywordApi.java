package com.github.zighang_zighang.domain.resume.__keyword.api;

import com.github.zighang_zighang.domain.resume.__keyword.dto.response.ResumeKeywordResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "[이력서 키워드]",
        description = "이력서/자기소개서 키워드 API"
)
public interface ResumeKeywordApi {

    @Operation(
            summary = "이력서 키워드 조회",
            description = "이력서에서 추출된 키워드를 조회합니다."
    )
    ApiResponse<ResumeKeywordResponse> getKeywords(@Parameter(hidden = true) User user);
}
