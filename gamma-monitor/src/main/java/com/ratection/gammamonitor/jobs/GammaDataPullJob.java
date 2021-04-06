package com.ratection.gammamonitor.jobs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ratection.gammamonitor.service.NettyTcpService;
import com.ratection.gammamonitor.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableScheduling
@Async
public class GammaDataPullJob {

    private SettingService settingService;
    private NettyTcpService nettyTcpService;

    private final static Cache<String, Long> cache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .concurrencyLevel(10)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();

    @Autowired
    GammaDataPullJob(SettingService settingService, NettyTcpService nettyTcpService) {
        this.settingService = settingService;
        this.nettyTcpService = nettyTcpService;
    }

    @Scheduled(cron = "0/5 * * * * *")
    public void run() {
        settingService.getAllRunningDevice()
                .forEach(device -> {
                    Boolean status = nettyTcpService.readRuningParam(device.getDeviceIp(), Integer.valueOf(device.getDeviceNum()));
                    if (!status) {
                        try {
                            Long count = cache.get(device.getDeviceIp(), () -> 0L);
                            if (count > 30) {
                                settingService.updateVoltageModuleStatus(device.getDeviceNum(), false);
                                settingService.updateGmModuleStatus(device.getDeviceNum(), false);
                            } else {
                                cache.put(device.getDeviceIp(), ++ count);
                            }
                        } catch (ExecutionException e) {
                            log.error("cache error.", e);
                        }
                    }
                });

    }
}
