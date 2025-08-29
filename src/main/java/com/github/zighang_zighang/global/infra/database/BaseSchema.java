package com.github.zighang_zighang.global.infra.database;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseSchema {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    UUID id;

    @Column(updatable = false, nullable = false)
    @CreatedDate
    LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    LocalDateTime updatedAt;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (!(obj instanceof BaseSchema that)) return false;
        if (this.id == null || that.id == null) return false;

        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(id);
    }
}