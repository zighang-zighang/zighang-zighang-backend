package com.github.zighang_zighang.domain.resume.__keyword.dto.response;

import com.github.zighang_zighang.domain.resume.__keyword.entity.ResumeKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ResumeKeywordResponse {

    @Schema(description = "키워드")
    List<String> keywords;

    public static ResumeKeywordResponse from(List<ResumeKeyword> resumeKeywords) {

        return ResumeKeywordResponse.of(resumeKeywords.stream().map(ResumeKeyword::getKeyword).toList());
    }
}
