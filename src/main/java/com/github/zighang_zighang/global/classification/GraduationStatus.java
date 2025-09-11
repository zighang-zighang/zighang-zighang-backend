package com.github.zighang_zighang.global.classification;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GraduationStatus {
    재학중,
    휴학중,
    졸업유예,
    졸업
}