package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecruitmentViewRepository extends JpaRepository<RecruitmentView, UUID> {

    int countByRecruitmentId(UUID recruitmentId);
}
