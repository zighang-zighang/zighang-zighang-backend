package com.github.zighang_zighang.domain.user.repository;

import com.github.zighang_zighang.domain.user.entity.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProviderRepository extends JpaRepository<UserProvider, UUID> {
}
