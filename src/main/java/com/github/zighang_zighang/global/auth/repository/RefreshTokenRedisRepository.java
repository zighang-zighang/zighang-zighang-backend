package com.github.zighang_zighang.global.auth.repository;

import com.github.zighang_zighang.global.auth.schema.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

    List<RefreshToken> findByUserId(String userId);

    void deleteByUserId(String userId);
}
