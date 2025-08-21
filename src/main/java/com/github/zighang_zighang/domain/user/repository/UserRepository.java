package com.github.zighang_zighang.domain.user.repository;

import com.github.zighang_zighang.domain.user.schema.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
