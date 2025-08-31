package com.github.zighang_zighang.domain.recruitment.constant;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CompanySize {
    대기업,
    유니콘,
    스타트업,
    공기업,
    중견기업,
    중소기업,
    외국계,
    공공기관,
    기타,
}
