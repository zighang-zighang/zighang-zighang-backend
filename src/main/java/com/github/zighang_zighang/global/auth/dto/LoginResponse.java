package com.github.zighang_zighang.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String email;
    private String name;
    private String userId;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresIn; // 초 단위
}
