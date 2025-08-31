package com.github.zighang_zighang.domain.recruitment.constant;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum EndType {
    상시_채용,
    채용_시_마감,
    기한_설정,
}
