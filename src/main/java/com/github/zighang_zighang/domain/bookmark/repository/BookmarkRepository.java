package com.github.zighang_zighang.domain.bookmark.repository;

import com.github.zighang_zighang.domain.bookmark.entity.Bookmark;
import com.github.zighang_zighang.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    Page<Bookmark> findAllByUser(User user, Pageable pageable);

    Optional<Bookmark> findByUserAndRecruitmentId(User user, UUID recruitmentId);

    boolean existsByUserAndRecruitmentId(User user, UUID recruitmentId);
}
