package hhplus.common.enums;

import lombok.Getter;

@Getter
public enum ResponseCodeEnum {

    SUCCESS(0x0000, "success"),
    FAILED(-0x0001, "failed"),
    ERROR(-0x0002,"error"),

    VALIDATION_ERROR(-0x1003, "잘못된 인자 입니다."),

    ;
    private final int code;

    private final String message;

    ResponseCodeEnum(int code, String message){
        this.code = code;
        this.message = message;
    }
}