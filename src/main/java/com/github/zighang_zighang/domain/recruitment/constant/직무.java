package com.github.zighang_zighang.domain.recruitment.constant;

import com.github.zighang_zighang.domain.recruitment.constant.직군.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum 직무 {
    IT_개발(com.github.zighang_zighang.domain.recruitment.constant.직군.IT_개발.class),
    AI_데이터(com.github.zighang_zighang.domain.recruitment.constant.직군.AI_데이터.class),
    게임(com.github.zighang_zighang.domain.recruitment.constant.직군.게임.class),
    디자인(com.github.zighang_zighang.domain.recruitment.constant.직군.디자인.class),
    기획_전략(com.github.zighang_zighang.domain.recruitment.constant.직군.기획_전략.class),
    마케팅_광고(com.github.zighang_zighang.domain.recruitment.constant.직군.마케팅_광고.class),
    상품기획_MD(com.github.zighang_zighang.domain.recruitment.constant.직군.상품기획_MD.class),
    영업(com.github.zighang_zighang.domain.recruitment.constant.직군.영업.class),
    무역_물류(com.github.zighang_zighang.domain.recruitment.constant.직군.무역_물류.class),
    운송_배송(com.github.zighang_zighang.domain.recruitment.constant.직군.운송_배송.class),
    법률_법무(com.github.zighang_zighang.domain.recruitment.constant.직군.법률_법무.class),
    HR_총무(com.github.zighang_zighang.domain.recruitment.constant.직군.HR_총무.class),
    회계_재무_세무(com.github.zighang_zighang.domain.recruitment.constant.직군.회계_재무_세무.class),
    증권_운용(com.github.zighang_zighang.domain.recruitment.constant.직군.증권_운용.class),
    은행_카드_보험(com.github.zighang_zighang.domain.recruitment.constant.직군.은행_카드_보험.class),
    엔지니어링_RND(com.github.zighang_zighang.domain.recruitment.constant.직군.엔지니어링_RND.class),
    건설_건축(com.github.zighang_zighang.domain.recruitment.constant.직군.건설_건축.class),
    생산_기능직(com.github.zighang_zighang.domain.recruitment.constant.직군.생산_기능직.class),
    의료_보건(com.github.zighang_zighang.domain.recruitment.constant.직군.의료_보건.class),
    공공_복지(com.github.zighang_zighang.domain.recruitment.constant.직군.공공_복지.class),
    교육(com.github.zighang_zighang.domain.recruitment.constant.직군.교육.class),
    미디어_엔터(com.github.zighang_zighang.domain.recruitment.constant.직군.미디어_엔터.class),
    고객상담_TM(com.github.zighang_zighang.domain.recruitment.constant.직군.고객상담_TM.class),
    서비스(com.github.zighang_zighang.domain.recruitment.constant.직군.서비스.class),
    식음료(com.github.zighang_zighang.domain.recruitment.constant.직군.식음료.class),
    ;

    private final Class<? extends Enum<? extends 직군>> subType;

    public 직군[] getSubTypes() {

        return (직군[]) subType.getEnumConstants();
    }
}
