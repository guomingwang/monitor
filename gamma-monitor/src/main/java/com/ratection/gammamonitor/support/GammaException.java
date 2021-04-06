package com.ratection.gammamonitor.support;

import lombok.Data;
import lombok.Getter;

@Data
public class GammaException extends RuntimeException {
    private Error error;
    private Throwable cause;

    public GammaException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public GammaException(String message, Throwable cause) {
        super(message, cause);
    }

    public enum Error {
        //system
        UNKNOWN_ERROR(11000, "internal server error."),
        PARAMETER_ERROR(11001, "Request parameter format error."),

        //auth
        AUTH_FAILED(12000, "auth failed, please check your username and password."),
        TOEKN_IS_NULL(12001, "token is null"),

        //setting
        DEVICE_EXIST(13000, "device exist"),
        DEVICE_NOT_EXIST(13001, "device not exist"),
        DEVICE_NAME_EXIST(13002, "device name exist"),
        DEVICE_IP_EXIST(13003, "device ip exist"),
        DEVICE_CONNECT_FAILED(13004, "device connect failed"),

        //metric
        TIME_RANGE_ERROR(14000, "time range error");

        @Getter
        private Integer code;
        @Getter
        private String message;

        Error(Integer code, String message) {
            this.code = code;
            this.message = message;
        }
    }

}
