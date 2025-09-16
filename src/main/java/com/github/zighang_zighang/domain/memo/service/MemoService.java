package com.github.zighang_zighang.domain.memo.service;

import com.github.zighang_zighang.domain.memo.dto.request.DeleteMemosByRecruitmentIdsRequest;
import com.github.zighang_zighang.domain.memo.dto.request.UpsertMemoRequest;
import com.github.zighang_zighang.domain.memo.dto.response.AllMemosResponse;
import com.github.zighang_zighang.domain.memo.dto.response.MemoResponse;
import com.github.zighang_zighang.domain.memo.dto.response.MemosResponse;
import com.github.zighang_zighang.domain.memo.entity.Memo;
import com.github.zighang_zighang.domain.memo.exception.MemoExceptions;
import com.github.zighang_zighang.domain.memo.repository.MemoRepository;
import com.github.zighang_zighang.domain.recruitment.exception.RecruitmentExceptions;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.domain.recruitment.service.RecruitmentService;
import com.github.zighang_zighang.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final RecruitmentService recruitmentService;

    private final RecruitmentRepository recruitmentRepository;
    private final MemoRepository memoRepository;

    public AllMemosResponse getAllMemos(User user) {

        List<Memo> memos = memoRepository.findAllByUserOrderByCreatedAtDesc(user);

        return AllMemosResponse.from(memos, recruitmentService);
    }

    public MemosResponse getMemos(User user, UUID recruitmentId) {

        List<Memo> memos = Optional.ofNullable(recruitmentId)
                .map(id -> memoRepository.findAllByUserAndRecruitmentIdOrderByCreatedAtDesc(user, id))
                .orElseGet(() -> memoRepository.findAllByUserOrderByCreatedAtDesc(user));

        return MemosResponse.from(memos, recruitmentService);
    }

    @Transactional
    public MemoResponse createMemo(User user, UUID recruitmentId, UpsertMemoRequest request) {

        recruitmentRepository.findById(recruitmentId)
                .orElseThrow(RecruitmentExceptions.NOT_FOUND::toException);

        Memo memo = memoRepository.save(
                Memo.builder()
                        .user(user)
                        .recruitmentId(recruitmentId)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .build()
        );

        return MemoResponse.from(memo, recruitmentService);
    }

    @Transactional
    public MemoResponse updateMemo(User user, UUID memoId, UpsertMemoRequest request) {

        Memo memo = memoRepository.findByIdAndUser(memoId, user)
                .orElseThrow(MemoExceptions.NOT_FOUND::toException);

        memo.setTitle(request.getTitle());
        memo.setContent(request.getContent());

        return MemoResponse.from(memo, recruitmentService);
    }

    @Transactional
    public void deleteMemo(User user, UUID memoId) {

        Memo memo = memoRepository.findByIdAndUser(memoId, user)
                .orElseThrow(MemoExceptions.NOT_FOUND::toException);

        System.out.println(memo);

        memoRepository.delete(memo);
    }

    @Transactional
    public void deleteMemosByRecruitmentIds(User user, DeleteMemosByRecruitmentIdsRequest request) {

        memoRepository.deleteByUserAndRecruitmentIdIn(user, request.getRecruitments());
    }
}