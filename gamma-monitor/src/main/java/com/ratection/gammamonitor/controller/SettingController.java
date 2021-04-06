package com.ratection.gammamonitor.controller;

import com.google.common.base.Strings;
import com.ratection.gammamonitor.controller.dto.GammaResponse;
import com.ratection.gammamonitor.model.Setting;
import com.ratection.gammamonitor.service.NettyTcpService;
import com.ratection.gammamonitor.service.SettingService;
import com.ratection.gammamonitor.support.GammaException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/setting")
public class SettingController {
    private SettingService settingService;
    private NettyTcpService nettyTcpService;

    @Autowired
    public SettingController(SettingService settingService, NettyTcpService nettyTcpService) {
        this.settingService = settingService;
        this.nettyTcpService = nettyTcpService;
    }

    @ApiOperation(httpMethod = "GET", value = "get all setting", response = GammaResponse.class)
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public GammaResponse<?> getAllSetting() {

        return GammaResponse.builder().code(0).data(settingService.getAllSetting()).build();

    }

    @ApiOperation(httpMethod = "GET", value = "get system", response = GammaResponse.class)
    @RequestMapping(value = "/getSystem", method = RequestMethod.GET)
    public GammaResponse<?> getSystem() {

        return GammaResponse.builder().code(0).data(settingService.getSystem()).build();

    }

    @ApiOperation(httpMethod = "GET", value = "get device name", response = GammaResponse.class)
    @RequestMapping(value = "/getDeviceName", method = RequestMethod.GET)
    public GammaResponse<?> getDeviceName(@RequestParam(value = "system") String system) {

        return GammaResponse.builder().code(0).data(settingService.getDeviceName(system)).build();

    }

    @ApiOperation(httpMethod = "POST", value = "add setting", response = GammaResponse.class)
    @RequestMapping(value = "/addSetting", method = RequestMethod.POST)
    public GammaResponse<?> addSetting(@RequestBody Setting setting) {

        if (Strings.isNullOrEmpty(setting.getDeviceNum()) || Strings.isNullOrEmpty(setting.getDeviceIp())
                || Strings.isNullOrEmpty(setting.getSystem())) {
            throw new GammaException(GammaException.Error.PARAMETER_ERROR);
        }
        if (settingService.getByDeviceNum(setting.getDeviceNum()) != null) {
            throw new GammaException(GammaException.Error.DEVICE_EXIST);
        }
        if (settingService.getByDeviceIp(setting.getDeviceIp()) != null) {
            throw new GammaException(GammaException.Error.DEVICE_IP_EXIST);
        }
        if (Strings.isNullOrEmpty(setting.getDeviceName())) {
            setting.setDeviceName(setting.getDeviceNum());
        } else if (settingService.getByDeviceName(setting.getDeviceName()) != null) {
            throw new GammaException(GammaException.Error.DEVICE_NAME_EXIST);
        }
        if (setting.getVoltageModuleStatus() == null) {
            setting.setVoltageModuleStatus(false);
        }
        if (setting.getGmModuleStatus() == null) {
            setting.setGmModuleStatus(false);
        }
        if (setting.getAlarmThreshold() == null) {
            setting.setAlarmThreshold(0.0F);
            settingService.addSetting(setting);
            nettyTcpService.readAlarmThreshold(setting.getDeviceIp(), Integer.valueOf(setting.getDeviceNum()));
            return GammaResponse.ok();
        }

        nettyTcpService.setAlarmThreshold(setting.getDeviceIp(), Integer.valueOf(setting.getDeviceNum()), setting.getAlarmThreshold());
        settingService.addSetting(setting);
        return GammaResponse.ok();

    }

    @ApiOperation(httpMethod = "POST", value = "update setting", response = GammaResponse.class)
    @RequestMapping(value = "/updateSetting", method = RequestMethod.POST)
    public GammaResponse<?> updateSetting(@RequestBody Setting setting) {

        if (Strings.isNullOrEmpty(setting.getDeviceNum()) || Strings.isNullOrEmpty(setting.getDeviceIp())
                || setting.getDeviceIp().split("\\.").length != 4 || Strings.isNullOrEmpty(setting.getDeviceName())
                || Strings.isNullOrEmpty(setting.getSystem()) || setting.getAlarmThreshold() == null) {
            throw new GammaException(GammaException.Error.PARAMETER_ERROR);
        }
        Setting existSetting = settingService.getByDeviceNum(setting.getDeviceNum());
        if (existSetting == null) {
            throw new GammaException(GammaException.Error.DEVICE_NOT_EXIST);
        }

        Setting otherSetting = settingService.getByDeviceIp(setting.getDeviceIp());
        if (otherSetting != null && !otherSetting.getDeviceNum().equals(setting.getDeviceNum())) {
            throw new GammaException(GammaException.Error.DEVICE_IP_EXIST);
        }

        otherSetting = settingService.getByDeviceName(setting.getDeviceName());
        if (otherSetting != null && !otherSetting.getDeviceNum().equals(setting.getDeviceNum())) {
            throw new GammaException(GammaException.Error.DEVICE_NAME_EXIST);
        }

        nettyTcpService.setAlarmThreshold(existSetting.getDeviceIp(), Integer.valueOf(setting.getDeviceNum()), setting.getAlarmThreshold());
        //更新设备ip，如果setAlarmThreshold放在后面执行，不知道设备是否已经更新好了ip，此时发送setAlarmThreshold指令，无法确定用哪个ip
        if (!existSetting.getDeviceIp().equals(setting.getDeviceIp().trim())) {
            nettyTcpService.setDeviceIp(
                    existSetting.getDeviceIp(),
                    Integer.valueOf(setting.getDeviceNum()),
                    setting.getDeviceIp().trim()
            );
            settingService.updateGmModuleStatus(setting.getDeviceNum(), false);
            settingService.updateVoltageModuleStatus(setting.getDeviceNum(), false);
        }
        settingService.updateSetting(setting);
        return GammaResponse.ok();
    }

    @ApiOperation(httpMethod = "DELETE", value = "delete setting", response = GammaResponse.class)
    @RequestMapping(value = "/delete/{deviceNum}", method = RequestMethod.DELETE)
    public GammaResponse<?> deleteSetting(@PathVariable("deviceNum") String deviceNum) {
        Setting setting = settingService.getByDeviceNum(deviceNum);
        if (setting == null) {
            throw new GammaException(GammaException.Error.DEVICE_NOT_EXIST);
        }

        settingService.deleteSetting(deviceNum);
        return GammaResponse.ok();
    }

    @ApiOperation(httpMethod = "GET", value = "get voltage module status", response = GammaResponse.class)
    @RequestMapping(value = "/getVoltageModuleStatus", method = RequestMethod.GET)
    public GammaResponse<?> getVoltageModuleStatus(@RequestParam(value = "deviceNum") String deviceNum) {

        Setting device = settingService.getByDeviceNum(deviceNum);
        if (device == null) {
            throw new GammaException(GammaException.Error.DEVICE_NOT_EXIST);
        }

        nettyTcpService.readVoltageModuleStatus(device.getDeviceIp(), Integer.valueOf(deviceNum));
        return GammaResponse.builder().code(0).data(settingService.getVoltageModuleStatus(deviceNum)).build();

    }

    @ApiOperation(httpMethod = "GET", value = "get gm module status", response = GammaResponse.class)
    @RequestMapping(value = "/getGmModuleStatus", method = RequestMethod.GET)
    public GammaResponse<?> getGmModuleStatus(@RequestParam(value = "deviceNum") String deviceNum) {

        Setting device = settingService.getByDeviceNum(deviceNum);
        if (device == null) {
            throw new GammaException(GammaException.Error.DEVICE_NOT_EXIST);
        }

        nettyTcpService.readGmModuleStatus(device.getDeviceIp(), Integer.valueOf(deviceNum));
        return GammaResponse.builder().code(0).data(settingService.getGmModuleStatus(deviceNum)).build();

    }

    @ApiOperation(httpMethod = "POST", value = "set voltage module status", response = GammaResponse.class)
    @RequestMapping(value = "/setVoltageModuleStatus", method = RequestMethod.POST)
    public GammaResponse<?> setVoltageModuleStatus(@RequestBody Setting setting) {
        if (Strings.isNullOrEmpty(setting.getDeviceNum()) || setting.getVoltageModuleStatus() == null) {
            throw new GammaException(GammaException.Error.PARAMETER_ERROR);
        }
        Setting device = settingService.getByDeviceNum(setting.getDeviceNum());
        if (device == null) {
            throw new GammaException(GammaException.Error.DEVICE_NOT_EXIST);
        }

        Boolean setSuccess = nettyTcpService.setVoltageModuleStatus(
                device.getDeviceIp(),
                Integer.valueOf(setting.getDeviceNum()),
                setting.getVoltageModuleStatus()
        );
        if (!setSuccess) {
            throw new GammaException(GammaException.Error.DEVICE_CONNECT_FAILED);
        }
        settingService.updateVoltageModuleStatus(setting.getDeviceNum(), setting.getVoltageModuleStatus());
        return GammaResponse.ok();
    }

    @ApiOperation(httpMethod = "POST", value = "set module status", response = GammaResponse.class)
    @RequestMapping(value = "/setGmModuleStatus", method = RequestMethod.POST)
    public GammaResponse<?> setGmModuleStatus(@RequestBody Setting setting) {
        if (Strings.isNullOrEmpty(setting.getDeviceNum()) || setting.getGmModuleStatus() == null) {
            throw new GammaException(GammaException.Error.PARAMETER_ERROR);
        }
        Setting device = settingService.getByDeviceNum(setting.getDeviceNum());
        if (device == null) {
            throw new GammaException(GammaException.Error.DEVICE_NOT_EXIST);
        }

        Boolean setSuccess = nettyTcpService.setGmModuleStatus(
                device.getDeviceIp(),
                Integer.valueOf(setting.getDeviceNum()),
                setting.getGmModuleStatus()
        );
        if (!setSuccess) {
            throw new GammaException(GammaException.Error.DEVICE_CONNECT_FAILED);
        }
        settingService.updateGmModuleStatus(setting.getDeviceNum(), setting.getGmModuleStatus());
        return GammaResponse.ok();
    }
}
