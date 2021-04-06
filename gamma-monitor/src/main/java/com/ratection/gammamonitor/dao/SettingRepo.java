package com.ratection.gammamonitor.dao;

import com.ratection.gammamonitor.model.Setting;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SettingRepo {

    @Select("SELECT * from gamma_monitor_setting")
    @Results(id = "SettingMapping", value = {
            @Result(property = "id", column = "id", javaType = Long.class),
            @Result(property = "deviceNum", column = "device_num", javaType = String.class),
            @Result(property = "deviceIp", column = "device_ip", javaType = String.class),
            @Result(property = "deviceName", column = "device_name", javaType = String.class),
            @Result(property = "system", column = "system", javaType = String.class),
            @Result(property = "alarmThreshold", column = "alarm_threshold", javaType = Float.class),
            @Result(property = "voltageModuleStatus", column = "voltage_module_status", javaType = Boolean.class),
            @Result(property = "gmModuleStatus", column = "gm_module_status", javaType = Boolean.class),
    })
    List<Setting> getAllSetting();

    @Select("SELECT distinct(system) from gamma_monitor_setting")
    List<String> getSystem();

    @Select("SELECT device_name from gamma_monitor_setting where system=#{system}")
    List<String> getDeviceNameBySystem(@Param("system") String system);

    @Select("SELECT * from gamma_monitor_setting where voltage_module_status = true and gm_module_status = true")
    @ResultMap("SettingMapping")
    List<Setting> getAllRunningDevice();

    @Select("SELECT * from gamma_monitor_setting LIMIT #{start},#{num}")
    @ResultMap("SettingMapping")
    List<Setting> getByPaging(@Param("start") Integer start,
                              @Param("num") Integer num);

    @Select("SELECT * from gamma_monitor_setting where device_num=#{deviceNum}")
    @ResultMap("SettingMapping")
    Setting getByDeviceNum(String deviceNum);

    @Select("SELECT * from gamma_monitor_setting where device_ip=#{deviceIp}")
    @ResultMap("SettingMapping")
    Setting getByDeviceIp(String deviceIp);

    @Select("SELECT * from gamma_monitor_setting where device_name=#{deviceName}")
    @ResultMap("SettingMapping")
    Setting getByDeviceName(String deviceName);

    @Select("<script>" +
            "SELECT device_num from gamma_monitor_setting where device_name in (" +
            "<foreach collection=\"names\" item=\"name\" index=\"index\"  separator=\",\">" +
            "#{name}" +
            "</foreach>" +
            ")" +
            "</script>")
    List<String> getByDeviceNameList(@Param("names") List<String> deviceNameList);

    @Select("SELECT count(*) from gamma_monitor_setting")
    Long getTotalCount();

    @Select("insert into gamma_monitor_setting " +
            "(device_num, device_ip, device_name, system, alarm_threshold, voltage_module_status, gm_module_status) " +
            "values (#{deviceNum}, #{deviceIp}, #{deviceName}, #{system}, #{alarmThreshold}, #{voltageModuleStatus}, #{gmModuleStatus})")
    void insert(Setting setting);

    @Update("update gamma_monitor_setting set alarm_threshold=#{alarmThreshold} where device_num=#{deviceNum}")
    void updateAlarmThreshold(@Param("deviceNum") String deviceNum,
                              @Param("alarmThreshold") Float alarmThreshold);

    @Update("update gamma_monitor_setting set voltage_module_status=#{voltageModuleStatus} where " +
            "device_num=#{deviceNum}")
    void updateVoltageModuleStatus(@Param("deviceNum") String deviceNum,
                                   @Param("voltageModuleStatus") Boolean voltageModuleStatus);

    @Update("update gamma_monitor_setting set gm_module_status=#{gmModuleStatus} where device_num=#{deviceNum}")
    void updateGmModuleStatus(@Param("deviceNum") String deviceNum,
                              @Param("gmModuleStatus") Boolean gmModuleStatus);

    @Update("update gamma_monitor_setting set device_name=#{deviceName}, device_ip=#{deviceIp}, system=#{system}, alarm_threshold=#{alarmThreshold} " +
            "where device_num=#{deviceNum}")
    void updateSetting(Setting setting);

    @Delete("delete from gamma_monitor_setting where device_num=#{deviceNum}")
    void deleteSetting(String deviceNum);

    @Select("SELECT voltage_module_status from gamma_monitor_setting where device_num=#{deviceNum}")
    Boolean getVoltageModuleStatus(String deviceNum);

    @Select("SELECT gm_module_status from gamma_monitor_setting where device_num=#{deviceNum}")
    Boolean getGmModuleStatus(String deviceNum);

}
