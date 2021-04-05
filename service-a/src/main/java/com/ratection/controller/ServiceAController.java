package com.ratection.controller;

import com.ratection.feign.ServiceBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wgm
 * @since 2021/4/4
 */
@RestController
@RefreshScope
@RequestMapping("a")
public class ServiceAController {

    @Value("${hi}")
    private String hi;

    @Autowired
    private ServiceBService serviceBService;

    @GetMapping("testOpenFeign")
    public String testOpenFeign() {
        return serviceBService.testOpenFeign();
    }

    @GetMapping("testConfig")
    public String testConfig() {
        return hi;
    }
}
