package com.mkw.hometax.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Constant {

    // Getter가 정의되어 있어야 .getCode()같은 것으로 내부 값을 가져올 수 있다. interface를 쓰는 경우는 추가적으로 인터페이스 쪽에 getter를 선언해야 할 수 있다.
    @Getter
    @RequiredArgsConstructor
    public enum MediaType {
        HalJsonUtf8("application/hal+json;charset=UTF-8");

        private final String code;

    }
}
