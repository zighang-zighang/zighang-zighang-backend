package com.github.zighang_zighang.domain.recruitment.repository.impl;

import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetRequest;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OpenSearchRecruitmentRepository implements RecruitmentRepository {

    private static final String INDEX = "recruitments";
    private static final Class<Recruitment> CLASS = Recruitment.class;

    private final OpenSearchClient client;

    @Override
    @SneakyThrows(IOException.class)
    public Optional<Recruitment> findById(UUID id) {

        return Optional.ofNullable(client.get(GetRequest.of(builder -> builder.index(INDEX).id(id.toString())), CLASS).source());
    }
}
