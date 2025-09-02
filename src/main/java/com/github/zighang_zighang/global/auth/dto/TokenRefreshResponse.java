package com.github.zighang_zighang.global.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString(exclude = {"accessToken", "refreshToken", "sessionId"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenRefreshResponse {
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String accessToken;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String refreshToken;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String sessionId;
    
    private LoginResponse userInfo;
}
