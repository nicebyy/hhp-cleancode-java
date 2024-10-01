package hhplus.common.exception;

import hhplus.common.enums.ResponseCodeEnum;

public class BusinessException extends CustomException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(ResponseCodeEnum code) {
        super(code);
    }

    public BusinessException(ResponseCodeEnum code, String message) {
        super(message);
    }
}
