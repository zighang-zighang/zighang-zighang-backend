package com.github.zighang_zighang.domain.resume.__keyword.repository;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.resume.__keyword.entity.ResumeKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResumeKeywordRepository extends JpaRepository<ResumeKeyword, UUID> {

    List<ResumeKeyword> findByUser(User user);

    void deleteAllByUser(User user);
}
