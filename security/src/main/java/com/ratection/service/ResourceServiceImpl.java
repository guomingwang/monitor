package com.ratection.service;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 资源与角色匹配关系管理业务类
 */
@Service
public class ResourceServiceImpl {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_TEST = "TEST";

    private Map<String, List<String>> resourceRolesMap;

    @Value("${spring.redis.key.auth}")
    private String key;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @PostConstruct
    public void init() {
        resourceRolesMap = new HashMap<>();
        resourceRolesMap.put("/c1/hello", CollUtil.toList(ROLE_ADMIN));
        resourceRolesMap.put("/c1/user/currentUser", CollUtil.toList(ROLE_ADMIN, ROLE_TEST));
        redisTemplate.opsForHash().putAll(key, resourceRolesMap);
    }
}
