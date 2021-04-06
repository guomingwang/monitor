package com.ratection.gammamonitor.support;

import com.google.common.base.Strings;
import com.ratection.gammamonitor.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private RedisService redisService;

    @Autowired
    public LoginInterceptor(RedisService redisService) {
        this.redisService = redisService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");

        //判断用户是否已经登录
        if(!Strings.isNullOrEmpty(token) && redisService.exist(token)) {
            //用户已经登录，验证通过，放行
            return true;
        } else {
            //验证还未登录
            response.setStatus(404);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

    }
}
