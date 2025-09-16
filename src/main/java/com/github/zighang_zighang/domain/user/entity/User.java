package com.github.zighang_zighang.domain.user.entity;

import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.global.classification.*;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<Job> interestedJobs = new ArrayList<>();
    // 직무
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<JobCategory> interestJobCategories = new ArrayList<>();
    // 경력
    @Min(0)
    @Max(10)
    private int careerYear;
    // 최종 학력 - 최종 학교
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    // 자기소개서
    // 최종 학력 - 졸업 구분
    @Enumerated(EnumType.STRING)
    private GraduationStatus graduationStatus;
    // 선호 근무 지역
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<Location> preferredRegions = new ArrayList<>();

    // 자기소개서
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Resume> resumes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserProvider> providers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<RecruitmentView> recruitmentViews = new ArrayList<>();

    public void updateOnboardingInfo(
            List<Job> interestedJobs,
            List<JobCategory> interestJobCategories,
            int careerYear,
            EducationLevel educationLevel,
            GraduationStatus graduationStatus,
            List<Location> preferredRegions
    ) {
        this.interestedJobs.clear();
        if (interestedJobs != null) this.interestedJobs.addAll(interestedJobs);
        this.interestJobCategories.clear();
        if (interestJobCategories != null) this.interestJobCategories.addAll(interestJobCategories);
        this.careerYear = careerYear;
        this.educationLevel = educationLevel;
        this.graduationStatus = graduationStatus;
        this.preferredRegions.clear();
        if (preferredRegions != null) this.preferredRegions.addAll(preferredRegions);
    }

}

