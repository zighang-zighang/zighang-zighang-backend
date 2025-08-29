package com.github.zighang_zighang.domain.user.repository;

import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.constant.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProviderRepository extends JpaRepository<UserProvider, UUID> {
    Optional<UserProvider> findByProviderIdAndType(String providerId, ProviderType type);
}
