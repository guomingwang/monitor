package com.ratection.gammamonitor.service;

import com.ratection.gammamonitor.controller.dto.PageResult;
import com.ratection.gammamonitor.dao.SettingRepo;
import com.ratection.gammamonitor.model.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettingService {

    private SettingRepo settingRepo;

    @Autowired
    public SettingService(SettingRepo settingRepo) {
        this.settingRepo = settingRepo;
    }

    public List<Setting> getAllSetting() {
        return settingRepo.getAllSetting();
    }

    public List<String> getSystem() {
        return settingRepo.getSystem();
    }

    public List<String> getDeviceName(String system) {
        return settingRepo.getDeviceNameBySystem(system);
    }

    public PageResult<Setting> getSettingByPaging(Integer pageNumber, Integer pageSize) {
        PageResult<Setting> pageResult = new PageResult<>();
        pageResult.setPageNumber(pageNumber);
        pageResult.setPageSize(pageSize);
        Integer start = (pageResult.getPageNumber() - 1) * pageResult.getPageSize();

        List<Setting> settingList = settingRepo.getByPaging(start, pageSize);
        Long totalCount = settingRepo.getTotalCount();

        return new PageResult<>(
                pageNumber,
                pageSize,
                totalCount,
                settingList
        );
    }

    public List<Setting> getAllRunningDevice() {
        return settingRepo.getAllRunningDevice();
    }

    public Setting getByDeviceNum(String deviceNum) {
        return settingRepo.getByDeviceNum(deviceNum);
    }

    public Setting getByDeviceIp(String deviceIp) {
        return settingRepo.getByDeviceIp(deviceIp);
    }

    public Setting getByDeviceName(String deviceName) {
        return settingRepo.getByDeviceName(deviceName);
    }

    public List<String> getByDeviceNameList(List<String> deviceNameList) {
        return settingRepo.getByDeviceNameList(deviceNameList);
    }

    public synchronized void addSetting(Setting setting) {
        if (settingRepo.getByDeviceNum(setting.getDeviceNum()) == null) {
            settingRepo.insert(setting);
        }
    }

    public void updateAlarmThreshold(String deviceNum, Float alarmThreshold) {
        settingRepo.updateAlarmThreshold(deviceNum, alarmThreshold);
    }

    public void updateVoltageModuleStatus(String deviceNum, Boolean voltageModuleStatus) {
        settingRepo.updateVoltageModuleStatus(deviceNum, voltageModuleStatus);
    }

    public void updateGmModuleStatus(String deviceNum, Boolean gmModuleStatus) {
        settingRepo.updateGmModuleStatus(deviceNum, gmModuleStatus);
    }

    public void updateSetting(Setting setting) {
        settingRepo.updateSetting(setting);
    }

    public void deleteSetting(String deviceNum) {
        settingRepo.deleteSetting(deviceNum);
    }

    public Boolean getVoltageModuleStatus(String deviceNum) {
        return settingRepo.getVoltageModuleStatus(deviceNum);
    }

    public Boolean getGmModuleStatus(String deviceNum) {
        return settingRepo.getGmModuleStatus(deviceNum);
    }
}
