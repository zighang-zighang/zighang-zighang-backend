package com.github.zighang_zighang.domain.user.entity;

import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.global.classification.EducationLevel;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.classification.Region;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseSchema {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    // 직군
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<Job> interestedJobs = new ArrayList<>();

    // 직무
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<JobCategory> interestJobCategories = new ArrayList<>();

    // 경력
    private int careerYears;

    // 최종 학력
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    // 선호 근무 지역
    @Enumerated(EnumType.STRING)
    private Region preferredRegion;

    // 자기소개서

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserProvider> providers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<RecruitmentView> recruitmentViews = new ArrayList<>();

    public void updateOnboardingInfo(
            List<Job> interestedJobs,
            List<JobCategory> interestJobCategories,
            int careerYears,
            EducationLevel educationLevel,
            Region preferredRegion
    ) {
        this.interestedJobs = interestedJobs;
        this.interestJobCategories = interestJobCategories;
        this.careerYears = careerYears;
        this.educationLevel = educationLevel;
        this.preferredRegion = preferredRegion;
    }

}

