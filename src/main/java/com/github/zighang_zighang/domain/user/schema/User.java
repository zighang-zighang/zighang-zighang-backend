package com.github.zighang_zighang.domain.user.schema;

import com.github.zighang_zighang.domain.recruitment.constant.규모;
import com.github.zighang_zighang.domain.recruitment.constant.직군.직군;
import com.github.zighang_zighang.domain.recruitment.constant.직무;
import com.github.zighang_zighang.domain.recruitment.constant.학력_조건;
import com.github.zighang_zighang.domain.user.constant.OauthProviderType;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@Document
@SuperBuilder(toBuilder = true)
public class User extends BaseSchema {

    String name;
    String email;
    String profileImage;
    String nickname;

    List<OauthProvider> oauthProvider;

    직무 jobCategory;
    List<직군> jobPosition;
    List<규모> companyScale;
    List<학력_조건> educationRequirement;
    CareerRange careerRange;
    String receiptEmail;

    @Getter
    @ToString
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CareerRange {

        int start;
        int end;
    }

    @Getter
    @ToString
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OauthProvider {

        OauthProviderType type;
        String providerId;
    }
}

