package com.github.zighang_zighang.domain.recruitment.repository.impl;

import com.github.zighang_zighang.domain.recruitment.constant.DeadlineType;
import com.github.zighang_zighang.domain.recruitment.constant.Education;
import com.github.zighang_zighang.domain.recruitment.constant.EmploymentType;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentViewRepository;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.classification.Location;
import com.github.zighang_zighang.global.response.PageInfo;
import com.github.zighang_zighang.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.GetRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.zighang_zighang.domain.recruitment.util.RecruitmentQueryBuilder.generateQuery;
import static com.github.zighang_zighang.domain.recruitment.util.RecruitmentQueryBuilder.generateRangeQuery;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OpenSearchRecruitmentRepository implements RecruitmentRepository {

    private static final String INDEX = "recruitments";
    private static final Class<Recruitment> CLASS = Recruitment.class;

    private final OpenSearchClient client;
    private final RecruitmentViewRepository recruitmentViewRepository;

    @SneakyThrows(IOException.class)
    public Optional<Recruitment> findById(UUID id) {

        return Optional.ofNullable(client.get(GetRequest.of(q -> q.index(INDEX).id(id.toString())), CLASS).source());
    }

    @Override
    @SneakyThrows(IOException.class)
    public PageResponse<Recruitment> findByFilters(
            List<Job> jobs,
            List<JobCategory> jobCategories,
            List<EmploymentType> employmentTypes,
            List<Education> educations,
            Integer minExperience,
            Integer maxExperience,
            List<Location> locations,
            List<DeadlineType> deadlineTypes,
            Integer page,
            Integer size
    ) {

        List<Query> queries = Stream.of(
                generateQuery(jobs, "jobs"),
                generateQuery(jobCategories, "jobCategories"),
                generateQuery(employmentTypes, "employmentTypes"),
                generateQuery(educations, "educations"),
                generateRangeQuery(minExperience, maxExperience),
                generateQuery(locations, "locations"),
                generateQuery(deadlineTypes, "deadlineType")
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

        return PageResponse.of(
                response.hits().hits().stream().map(Hit::source).toList(),
                PageInfo.of(size, page, response.hits().total() != null ? response.hits().total().value() : 0)
        );
    }

    @Override
    public List<Recruitment> findPopularRecruitmentIds(
            Job job,
            LocalDateTime viewCutoff,
            LocalDateTime bookmarkCutoff,
            LocalDateTime applicationCutoff,
            double viewWeight,
            double bookmarkWeight,
            double applicationWeight
    ) {

        return recruitmentViewRepository.findPopularRecruitmentIds(
                        viewCutoff, bookmarkCutoff, applicationCutoff,
                        viewWeight, bookmarkWeight, applicationWeight
                ).stream()
                .filter((id) -> id.length == 16)
                .map((id) -> {
                    ByteBuffer bb = ByteBuffer.wrap(id);
                    long high = bb.getLong();
                    long low = bb.getLong();
                    return new UUID(high, low);
                })
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(r -> job == null || r.getJobs().stream().anyMatch(job1 -> job1.name().equals(job.name())))
                .limit(5)
                .toList();
    }
}
