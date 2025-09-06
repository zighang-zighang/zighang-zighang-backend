package com.github.zighang_zighang.domain.memo.entity;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(indexes = {
        @Index(columnList = "user_id"),
        @Index(columnList = "recruitment_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo extends BaseSchema {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    UUID recruitmentId;

    @Column(length = 255)
    String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;
}