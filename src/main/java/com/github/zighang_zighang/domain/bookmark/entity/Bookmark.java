package com.github.zighang_zighang.domain.bookmark.entity;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        indexes = {
                @Index(columnList = "user_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "recruitment_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseSchema {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    UUID recruitmentId;
}

