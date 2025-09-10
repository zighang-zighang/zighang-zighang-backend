package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentApplication;
import com.github.zighang_zighang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecruitmentApplicationRepository extends JpaRepository<RecruitmentApplication, UUID> {

    boolean existsByUserAndRecruitmentId(User user, UUID recruitmentId);
}
