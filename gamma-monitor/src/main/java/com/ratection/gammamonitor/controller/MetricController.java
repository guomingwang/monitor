package com.ratection.gammamonitor.controller;

import com.google.common.base.Strings;
import com.ratection.gammamonitor.controller.dto.GammaResponse;
import com.ratection.gammamonitor.service.MetricService;
import com.ratection.gammamonitor.service.SettingService;
import com.ratection.gammamonitor.support.GammaException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/metric")
public class MetricController {
    private static final Long DEFAULT_TIME_RANGE = 21600000L; // 6 * 60 * 60 *1000

    private MetricService metricService;
    private SettingService settingService;

    @Autowired
    public MetricController(MetricService metricService, SettingService settingService) {
        this.metricService = metricService;
        this.settingService = settingService;
    }

    @ApiOperation(httpMethod = "GET", value = "get metrics", response = GammaResponse.class)
    @RequestMapping(value = "/getMetrics", method = RequestMethod.GET)
    public GammaResponse<?> getMetrics(@RequestParam(value = "system") String system,
                                       @RequestParam(value = "deviceName", required = false) String deviceName, //多个英文逗号隔开
                                       @RequestParam(value = "startTime", required = false) Long startTime,
                                       @RequestParam(value = "endTime", required = false) Long endTime) {
        if (Strings.isNullOrEmpty(system)) {
            throw new GammaException(GammaException.Error.PARAMETER_ERROR);
        }
        if (startTime == null) {
            startTime = new Date().getTime() - DEFAULT_TIME_RANGE;
        }
        if (endTime == null) {
            endTime = new Date().getTime();
        }
        if (startTime >= endTime) {
            throw new GammaException(GammaException.Error.TIME_RANGE_ERROR);
        }

        List<String> deviceNumList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(deviceName)) {
            deviceNumList = settingService.getByDeviceNameList(Arrays.stream(deviceName.split(",")).collect(Collectors.toList()));
        }

        return GammaResponse.builder().code(0).data(metricService.getMetrics(system, deviceNumList, startTime, endTime)).build();

    }

//    @ApiOperation(httpMethod = "GET", value = "get by paging", response = GammaResponse.class)
//    @RequestMapping(value = "/history", method = RequestMethod.GET)
//    public GammaResponse<?> history(@RequestParam(value = "system") String system,
//                                    @RequestParam(value = "deviceName", required = false) String deviceName,
//                                    @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
//                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
//                                    @RequestParam(value = "startTime", required = false) Long startTime,
//                                    @RequestParam(value = "endTime", required = false) Long endTime) {
//        if (Strings.isNullOrEmpty(system)) {
//            throw new GammaException(GammaException.Error.PARAMETER_ERROR);
//        }
//        if (startTime == null) {
//            startTime = new Date().getTime() - DEFAULT_TIME_RANGE;
//        }
//        if (endTime == null) {
//            endTime = new Date().getTime();
//        }
//        if (startTime >= endTime) {
//            throw new GammaException(GammaException.Error.TIME_RANGE_ERROR);
//        }
//
//        String deviceNum = "";
//        if (!Strings.isNullOrEmpty(deviceName)) {
//            if (settingService.getByDeviceName(deviceName) == null) {
//                throw new GammaException(GammaException.Error.DEVICE_NOT_EXIST);
//            }
//            deviceNum = settingService.getByDeviceName(deviceName).getDeviceNum();
//        }
//
//        return GammaResponse.builder().code(0).data(metricService.getMetricsByPaging(deviceNum, startTime, endTime, pageNumber, pageSize)).build();
//    }
}
