package com.github.zighang_zighang.domain.recruitment.repository.impl;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.GetRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.zighang_zighang.domain.recruitment.util.RecruitmentQueryBuilder.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OpenSearchRecruitmentRepository implements RecruitmentRepository {

    private static final String INDEX = "recruitments";
    private static final Class<Recruitment> CLASS = Recruitment.class;

    private final OpenSearchClient client;

    @Override
    @SneakyThrows(IOException.class)
    public Optional<Recruitment> findById(UUID id) {

        return Optional.ofNullable(client.get(GetRequest.of(q -> q.index(INDEX).id(id.toString())), CLASS).source());
    }

    @Override
    @SneakyThrows(IOException.class)
    public Page<Recruitment> findByFilters(
            List<Job> jobs,
            List<JobGroup> jobGroups,
            List<EmploymentType> employmentTypes,
            List<Education> educations,
            Integer minExperience,
            Integer maxExperience,
            List<Location> locations,
            List<EndType> endTypes,
            Integer page,
            Integer size
    ) {

        List<Query> queries = Stream.of(
                generateNestedQuery(jobs, "job_positions", "job"),
                generateNestedQuery(jobGroups, "job_positions", "job_group"),
                generateQuery(employmentTypes, "employment_type"),
                generateQuery(educations, "education"),
                generateRangeQuery(minExperience, maxExperience),
                generateQuery(locations, "location"),
                generateQuery(endTypes, "end_type")
        ).filter(Objects::nonNull).toList();

        Query query = Query.of(q ->
                queries.isEmpty()
                        ? q.matchAll(m -> m)
                        : q.bool(b -> b.must(queries))
        );

        SearchResponse<Recruitment> response = client.search(
                SearchRequest.of(builder -> builder
                        .index(INDEX)
                        .query(query)
                        .size(size)
                        .from(page * size)
                        .trackTotalHits(q -> q.enabled(true))
                ),
                CLASS
        );

        return new PageImpl<>(
                response.hits().hits().stream().map(Hit::source).toList(),
                PageRequest.of(page, size),
                response.hits().total() != null ? response.hits().total().value() : 0
        );
    }
}
