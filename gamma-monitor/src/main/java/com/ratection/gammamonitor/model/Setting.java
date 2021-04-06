package com.ratection.gammamonitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setting {

    private Long id;
    private String deviceNum;
    private String deviceIp;
    private String deviceName;
    private String system;
    private Float alarmThreshold;
    private Boolean voltageModuleStatus;
    private Boolean gmModuleStatus;
}