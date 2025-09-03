package com.github.zighang_zighang.domain.bookmark.repository;

import com.github.zighang_zighang.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
}
