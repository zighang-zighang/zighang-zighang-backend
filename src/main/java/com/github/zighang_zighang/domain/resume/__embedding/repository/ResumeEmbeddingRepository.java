package com.github.zighang_zighang.domain.resume.__embedding.repository;

import com.github.zighang_zighang.domain.resume.__embedding.entity.ResumeEmbedding;
import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ResumeEmbeddingRepository extends JpaRepository<ResumeEmbedding, UUID> {
    void deleteAllByResume(Resume resume);

    @Query("""
    SELECT re.embedding
    FROM ResumeEmbedding re
    JOIN re.resume r
    WHERE r.user = :user
    ORDER BY r.createdAt DESC
    LIMIT 1
    """)
    Optional<String> findLatestEmbeddingByUser(@Param("user") User user);

}
