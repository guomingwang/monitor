package com.ratection.api;

import lombok.Data;

/**
 * 自定义异常
 */
@Data
public class ServiceException extends RuntimeException {

    private long code;
    private String message;

    public ServiceException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public ServiceException(String message) {
        this.code = ResultCode.FAILED.getCode();
        this.message = message;
    }
}
