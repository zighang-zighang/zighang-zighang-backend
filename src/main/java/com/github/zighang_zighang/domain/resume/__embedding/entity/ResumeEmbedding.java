package com.github.zighang_zighang.domain.resume.__embedding.entity;

import com.github.zighang_zighang.domain.resume.entity.Resume;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(indexes = {
        @Index(name = "idx_resume_embedding__resume_id", columnList = "resume_id")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String embedding;
}
