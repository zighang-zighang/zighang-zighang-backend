package com.github.zighang_zighang.domain.resume.__embedding.repository;

import com.github.zighang_zighang.domain.resume.__embedding.entity.ResumeEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResumeEmbeddingRepository extends JpaRepository<ResumeEmbedding, UUID> {
}
