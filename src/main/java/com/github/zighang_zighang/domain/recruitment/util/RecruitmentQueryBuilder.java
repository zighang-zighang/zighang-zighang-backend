package com.github.zighang_zighang.domain.recruitment.util;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class RecruitmentQueryBuilder {

    public static Query generateQuery(List<? extends Enum<?>> enums, String fieldName) {

        if (CollectionUtils.isEmpty(enums)) return null;

        List<FieldValue> values = enums.stream().map(Enum::name).map(FieldValue::of).toList();

        return Query.of(q1 ->
                q1.terms(q2 -> q2.field(fieldName).terms(q3 -> q3.value(values)))
        );
    }

    public static Query generateRangeQuery(Integer minExperience, Integer maxExperience) {

        if (minExperience == null && maxExperience == null) return null;

        List<Query> queries = new ArrayList<>();

        if (minExperience != null) {
            queries.add(
                    Query.of(q1 ->
                            q1.range(q2 ->
                                    q2.field("maxExperience").gte(JsonData.of(minExperience))
                            )
                    )
            );
        }

        if (maxExperience != null) {
            queries.add(
                    Query.of(q1 ->
                            q1.range(q2 ->
                                    q2.field("minExperience").lte(JsonData.of(maxExperience))
                            )
                    )
            );
        }

        return Query.of(q -> q.bool(b -> b.must(queries)));
    }
}
