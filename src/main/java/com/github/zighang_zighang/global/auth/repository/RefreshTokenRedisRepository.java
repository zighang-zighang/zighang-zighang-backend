package com.github.zighang_zighang.global.auth.repository;

import com.github.zighang_zighang.global.auth.schema.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(String userId);

    void deleteByToken(String token);

    void deleteByUserId(String userId);
}
