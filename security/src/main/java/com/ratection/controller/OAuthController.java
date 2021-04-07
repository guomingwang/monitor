package com.ratection.controller;

import com.ratection.api.Result;
import com.ratection.domain.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * OAuth2获取令牌接口
 */
@RestController
@RequestMapping("oauth")
public class OAuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * OAuth2登录认证
     */
    @PostMapping("token")
    public Result<Token> token(Principal principal, @RequestParam Map<String, String> parameters)
            throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Token token = Token.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("bearer ").build();
        return Result.success(token);
    }
}
