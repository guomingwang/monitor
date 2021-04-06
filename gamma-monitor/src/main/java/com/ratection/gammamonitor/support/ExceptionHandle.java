package com.ratection.gammamonitor.support;

import com.ratection.gammamonitor.controller.dto.GammaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandle {
    @ResponseBody
    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = GammaException.class)
    public GammaResponse<?> hander(GammaException e) {
        return GammaResponse.error(e.getError());
    }
}
