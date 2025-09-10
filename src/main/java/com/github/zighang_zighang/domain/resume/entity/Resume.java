package com.github.zighang_zighang.domain.resume.entity;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume extends BaseSchema {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false)
    private UUID storageKey;

    @Column(nullable = false)
    private long size;

    @Lob
    private String content;

}
