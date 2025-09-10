package com.github.zighang_zighang.domain.recruitment.entity;

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
public class RecruitmentView extends BaseSchema {

    @Column(nullable = false)
    UUID recruitmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(nullable = false)
    String ipAddress;

    @Column(nullable = false, length = 1000)
    String userAgent;
}