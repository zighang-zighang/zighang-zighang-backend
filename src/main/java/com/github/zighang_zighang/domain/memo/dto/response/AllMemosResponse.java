package com.github.zighang_zighang.domain.memo.dto.response;

import com.github.zighang_zighang.domain.memo.entity.Memo;
import com.github.zighang_zighang.domain.recruitment.dto.response.SimpleRecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.service.RecruitmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AllMemosResponse {

    private final List<Item> memos;

    public static AllMemosResponse from(List<Memo> memos, RecruitmentService recruitmentService) {

        List<Item> items = memos.stream()
                .collect(Collectors.groupingBy(Memo::getRecruitmentId))
                .entrySet()
                .stream()
                .map(entry -> {
                    UUID recruitmentId = entry.getKey();
                    List<Memo> recruitmentMemos = entry.getValue();
                    
                    SimpleRecruitmentResponse recruitment = SimpleRecruitmentResponse.from(
                            recruitmentService.getRecruitment(recruitmentId)
                    );
                    
                    List<SimpleMemoResponse> memoResponses = recruitmentMemos.stream()
                            .map(SimpleMemoResponse::from)
                            .toList();
                    
                    return new Item(recruitment, memoResponses);
                })
                .toList();

        return AllMemosResponse.builder()
                .memos(items)
                .build();
    }

    public record Item(
            SimpleRecruitmentResponse recruitment,
            List<SimpleMemoResponse> memos
    ) {
    }
}