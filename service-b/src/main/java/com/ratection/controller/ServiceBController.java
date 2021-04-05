package com.ratection.controller;

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
@RequestMapping("b")
public class ServiceBController {

    @Value("${hi}")
    private String hi;

    @GetMapping("testOpenFeign")
    public String testOpenFeign() {
        return "testOpenFeign from b";
    }

    @GetMapping("testConfig")
    public String testConfig() {
        return hi;
    }
}
