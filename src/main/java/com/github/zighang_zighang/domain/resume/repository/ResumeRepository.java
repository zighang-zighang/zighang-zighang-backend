package com.github.zighang_zighang.domain.resume.repository;

import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    List<Resume> findByUser(User user);
}
