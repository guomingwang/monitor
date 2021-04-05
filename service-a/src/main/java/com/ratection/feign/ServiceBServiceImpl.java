package com.ratection.feign;

import org.springframework.stereotype.Component;

/**
 * @author wgm
 * @since 2021/4/4
 */
@Component
public class ServiceBServiceImpl implements ServiceBService {

    @Override
    public String testOpenFeign() {
        return "testOpenFeign from a";
    }
}
