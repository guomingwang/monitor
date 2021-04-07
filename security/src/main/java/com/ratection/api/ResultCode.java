package com.ratection.api;

/**
 * 结果码
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    USERNAME_PASSWORD_ERROR(400, "用户名或密码错误"),
    UNAUTHORIZED(401, "登录凭证已过期，请重新登录"),
    FORBIDDEN(403, "没有相关权限"),
    FAILED(500, "操作失败");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
