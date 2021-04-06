package com.ratection.gammamonitor.datasource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name = "gamma")
public class Metric {

    @Column(name = "time")
    public String time;

    @Column(name = "device-num")
    public String deviceNum;

    @Column(name = "device-name")
    public String deviceName;

    @Column(name = "system")
    public String system;

    @Column(name = "count")
    public Integer count;

    @Column(name = "temperature")
    public Double temperature;

    @Column(name = "humidity")
    public Double humidity;

    @Column(name = "voltage")
    public Integer voltage;

    @Column(name = "level")
    public String level;

    @Column(name = "gamma-value")
    public Double value;
}
