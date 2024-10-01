package hhplus.common.enums;

import lombok.Getter;

@Getter
public enum ResponseCodeEnum {

    SUCCESS(0x0000, "success"),
    FAILED(-0x0001, "failed"),
    ERROR(-0x0002,"error"),

    VALIDATION_ERROR(-0x1001, "잘못된 인자 입니다."),

    USER_NOT_FOUND(-0x2001, "존재 하지 않는 유저 입니다."),

    LECTURE_NOT_FOUND(-0x3001, "존재 하지 않는 강의 입니다."),
    NO_REMAINING_REGISTRATION(-0x3002, "잔여 좌석이 없습니다."),
    ALREADY_APPLIED_LECTURE(-0x3003, "이미 신청한 강의 입니다."),
    NO_REGISTRATION(-0x3004, "신청한 강좌가 없습니다."),

    ;
    private final int code;

    private final String message;

    ResponseCodeEnum(int code, String message){
        this.code = code;
        this.message = message;
    }
}