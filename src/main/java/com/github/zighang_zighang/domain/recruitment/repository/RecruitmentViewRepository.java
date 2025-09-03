package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RecruitmentViewRepository extends JpaRepository<RecruitmentView, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean existsByUserAndRecruitmentId(User user, UUID recruitmentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RecruitmentView rv set rv.viewCount = rv.viewCount + 1 where rv.user = :user and rv.recruitmentId = :recruitmentId")
    void incrementViewCount(User user, UUID recruitmentId);
}
