package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecruitmentViewRepository extends JpaRepository<RecruitmentView, UUID> {

    List<RecruitmentView> findByUserAndRecruitmentId(User user, UUID recruitmentId);
}
