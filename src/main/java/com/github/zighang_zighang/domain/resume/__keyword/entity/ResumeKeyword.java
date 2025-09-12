package com.github.zighang_zighang.domain.resume.__keyword.entity;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(columnList = "user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeKeyword extends BaseSchema {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    String keyword;
}

