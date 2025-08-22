package com.github.zighang_zighang.domain.recruitment.schema;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@Document
public class Recruitment extends BaseSchema {

    플랫폼 platform;
    직무 jobCategory;
    String title;
    LocalDateTime deadline;
    String applicationUrl;
    Company company;
    Metadata metadata;
    int viewCount;

    @Getter
    @ToString
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Company {

        String image;
        String name;
    }

    @Getter
    @ToString
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CareerRange {

        int start;
        int end;

        public static CareerRange of(int start, int end) {

            if (start < 0 || end < 0) throw new IllegalArgumentException("start/end must be ≥ 0");
            if (start > end) throw new IllegalArgumentException("start must be ≤ end");

            return new CareerRange(start, end);
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Metadata {

        채용_유형 recruitmentType;
        학력_조건 educations;
        CareerRange careerRange;
        지역 region;
        마감_유형 deadlineType;
    }
}

