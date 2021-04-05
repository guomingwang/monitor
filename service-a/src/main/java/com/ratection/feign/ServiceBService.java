package com.ratection.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wgm
 * @since 2021/4/4
 */
@FeignClient(value = "service-b", fallback = ServiceBServiceImpl.class)
public interface ServiceBService {

    @GetMapping("b/testOpenFeign")
    String testOpenFeign();
}
