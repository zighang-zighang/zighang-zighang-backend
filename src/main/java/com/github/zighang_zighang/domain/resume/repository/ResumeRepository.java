package com.github.zighang_zighang.domain.resume.repository;

import com.github.zighang_zighang.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
}
