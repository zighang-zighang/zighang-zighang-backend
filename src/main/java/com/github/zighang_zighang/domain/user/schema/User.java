package com.github.zighang_zighang.domain.user.schema;

import com.github.zighang_zighang.domain.recruitment.constant.규모;
import com.github.zighang_zighang.domain.recruitment.constant.직군.직군;
import com.github.zighang_zighang.domain.recruitment.constant.직무;
import com.github.zighang_zighang.domain.recruitment.constant.학력_조건;
import com.github.zighang_zighang.domain.user.constant.OAuthProviderType;
import com.github.zighang_zighang.global.infra.database.BaseSchema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@Document
@SuperBuilder(toBuilder = true)
@CompoundIndexes({
    @CompoundIndex(
        name = "uniq_oauth_provider",
        def = "{'oauthProvider.type': 1, 'oauthProvider.providerId': 1}",
        unique = true
    )
})
public class User extends BaseSchema {

    String name;

    @Indexed(unique = true)
    String email;

    String profileImage;

    String nickname;

    @Builder.Default
    List<OAuthProvider> oauthProviders = List.of();

    직무 jobCategory;

    @Builder.Default
    List<직군> jobPosition = List.of();

    @Builder.Default
    List<규모> companyScale = List.of();

    @Builder.Default
    List<학력_조건> educationRequirement = List.of();

    CareerRange careerRange;

    String receiptEmail;

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
    public static class OAuthProvider {

        OAuthProviderType type;
        String providerId;
    }
}

