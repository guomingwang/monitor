package com.ratection.api;

import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局处理异常
 */
@RestControllerAdvice
@Log
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Result serviceExceptionHandler(ServiceException e) {
        log.warning(e.getMessage());
        return Result.failed(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public Result throwableHandler(Throwable e) {
        log.warning(e.getMessage());
        return Result.failed(e.getMessage());
    }
}
