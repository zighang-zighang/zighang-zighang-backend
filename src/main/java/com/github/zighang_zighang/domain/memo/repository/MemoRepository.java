package com.github.zighang_zighang.domain.memo.repository;

import com.github.zighang_zighang.domain.memo.entity.Memo;
import com.github.zighang_zighang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemoRepository extends JpaRepository<Memo, UUID> {

    List<Memo> findAllByUserAndRecruitmentIdOrderByCreatedAtDesc(User user, UUID recruitmentId);
    List<Memo> findAllByUserOrderByCreatedAtDesc(User user);

    Optional<Memo> findByIdAndUser(UUID id, User user);

    void deleteByUserAndRecruitmentIdIn(User user, List<UUID> recruitmentIds);
}