package com.github.zighang_zighang.global.classification;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum EducationLevel {
    초등학교,
    중학교,
    고등학교,
    대학교_2_3년,
    대학교_4년,
    대학원_석사,
    대학원_박사
}
