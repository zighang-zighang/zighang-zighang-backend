package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;

import java.util.Optional;
import java.util.UUID;

public interface RecruitmentRepository {

    Optional<Recruitment> findById(UUID id);
}
