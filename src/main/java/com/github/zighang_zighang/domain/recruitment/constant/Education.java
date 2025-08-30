package com.github.zighang_zighang.domain.recruitment.constant;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Education {
    학력_무관,
    고졸,
    초대졸,
    학사,
    석사,
    박사,
}
