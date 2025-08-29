package com.github.zighang_zighang.domain.user.entity;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_provider_type_provider_id", columnNames = {"type", "providerId"}),
        @UniqueConstraint(name = "unique_user_provider_user_type", columnNames = {"user", "type"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProvider extends BaseSchema {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ProviderType type;

    @Column(nullable = false)
    String providerId;
}

