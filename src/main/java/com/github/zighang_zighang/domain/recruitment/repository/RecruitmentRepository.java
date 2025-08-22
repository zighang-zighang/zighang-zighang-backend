package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.schema.Recruitment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecruitmentRepository extends MongoRepository<Recruitment, String> {
}
