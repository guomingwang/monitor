package com.ratection.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 网关白名单配置
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ConfigurationProperties(prefix="secure.ignore")
@Component
@Log
public class IgnoreUrlsConfig {

    private List<String> urls;

    @PostConstruct
    public void init() {
        log.info(String.valueOf(urls));
    }
}
