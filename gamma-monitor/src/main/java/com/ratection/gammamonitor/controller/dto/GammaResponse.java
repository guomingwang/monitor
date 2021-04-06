package com.ratection.gammamonitor.controller.dto;

import com.ratection.gammamonitor.support.GammaException;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GammaResponse<R> {
    private Integer code;
    private String message;
    private R data;

    public static GammaResponse<?> ok() {
        return GammaResponse.builder().code(0).build();
    }

    public static GammaResponse<?> fail() {
        return GammaResponse.builder().code(1).build();
    }

    public static GammaResponse<?> error(GammaException.Error error) {
        return GammaResponse.builder().code(error.getCode()).message(error.getMessage()).build();
    }

}
