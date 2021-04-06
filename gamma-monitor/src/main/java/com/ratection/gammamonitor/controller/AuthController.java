package com.ratection.gammamonitor.controller;

import com.google.common.base.Strings;
import com.ratection.gammamonitor.controller.dto.GammaResponse;
import com.ratection.gammamonitor.service.AuthService;
import com.ratection.gammamonitor.service.RedisService;
import com.ratection.gammamonitor.support.GammaException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    private RedisService redisService;

    @Autowired
    public AuthController(AuthService authService, RedisService redisService) {
        this.authService = authService;
        this.redisService = redisService;
    }

    @ApiOperation(httpMethod = "POST", value = "login")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public GammaResponse<?> login(
            @RequestParam String username,
            @RequestParam String password
    ) {

        String token = authService.getToken(username, password);
        if (token == null) {
            throw new GammaException(GammaException.Error.AUTH_FAILED);
        }

        log.info("token: {}", token);
        redisService.addToken(token);

        Map<String, String> result = new HashMap<>();
        result.put("token", token);

        return GammaResponse.builder().code(0).data(result).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/logout")
    public GammaResponse<?> logout(HttpServletRequest request) {

        String token = request.getHeader("token");
        if (Strings.isNullOrEmpty(token)) {
            throw new GammaException(GammaException.Error.TOEKN_IS_NULL);
        }
        redisService.clearToken(token);

        return GammaResponse.ok();
    }

}


