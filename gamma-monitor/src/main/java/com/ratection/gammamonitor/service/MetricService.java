package com.ratection.gammamonitor.service;

import com.ratection.gammamonitor.controller.dto.PageResult;
import com.ratection.gammamonitor.datasource.InfluxDataSource;
import com.ratection.gammamonitor.datasource.dto.Metric;
import com.ratection.gammamonitor.model.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetricService {

    @Value("${influxdb.measurement}")
    private String measurement;

    private InfluxDataSource influxDataSource;

    @Autowired
    public MetricService(InfluxDataSource influxDataSource) {
        this.influxDataSource = influxDataSource;
    }

    public List<Metric> getMetrics(String system, List<String> deviceNumList, Long startTime, Long endTime) {
        return influxDataSource.getMetrics(measurement, system, deviceNumList, startTime, endTime);
    }

    /*
    public PageResult<Metric> getMetricsByPaging(String deviceNum, Long startTime, Long endTime, Integer pageNumber, Integer pageSize) {
        PageResult<Setting> pageResult = new PageResult<>();
        pageResult.setPageNumber(pageNumber);
        pageResult.setPageSize(pageSize);

        List<Metric> metricList = influxDataSource.getMetricsByPaging(measurement, deviceNum, startTime, endTime, pageNumber, pageSize);
        Long totalCount = influxDataSource.getTotalCount(measurement, deviceNum, startTime, endTime);

        return new PageResult<>(
                pageNumber,
                pageSize,
                totalCount,
                metricList
        );
    }
    */
}
