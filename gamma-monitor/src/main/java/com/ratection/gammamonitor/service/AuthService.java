package com.ratection.gammamonitor.service;

import com.ratection.gammamonitor.support.AccessTokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthService {

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    public String getToken(String username, String password) {

        if (username.equals(this.username) && password.equals(this.password)) {

            try {
                return AccessTokenGenerator.getInstance().getAccessToken();
            } catch (OAuthSystemException e) {
                log.error("generate token error.", e);
            }
        }
        return null;
    }
}
