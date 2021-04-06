package com.ratection.gammamonitor.service;

import com.ratection.gammamonitor.client.NettyTcpClient;
import com.ratection.gammamonitor.client.dto.RequestFrame;
import com.ratection.gammamonitor.support.HexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class NettyTcpService {

    private NettyTcpClient nettyTcpClient;

    @Autowired
    public NettyTcpService(NettyTcpClient nettyTcpClient) {
        this.nettyTcpClient = nettyTcpClient;
    }

    public void readAlarmThreshold(String deviceIp, Integer deviceNum) {
        sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x83,
                (char) 0xa6,
                (char) 0x01,
                (char) 0x00,
                null
        );
    }

    public void setDeviceIp(String deviceIp, Integer deviceNum, String newDeviceIp) {

        String ipHex = Arrays.stream(newDeviceIp.split("\\."))
                .map(e -> HexUtil.intToHexString(Integer.valueOf(e)))
                .collect(Collectors.joining(""));
        sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x82,
                (char) 0xa1,
                (char) 0x00,
                (char) 0x04,
                HexUtil.hexToCharArray(ipHex)
        );
    }

    public void setAlarmThreshold(String deviceIp, Integer deviceNum, Float alarmThreshold) {

        String dataHex = HexUtil.floatToInverseHex(alarmThreshold);
        sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x83,
                (char) 0xa6,
                (char) 0x00,
                (char) 0x04,
                HexUtil.hexToCharArray(dataHex)
        );
    }

    public void readVoltageModuleStatus(String deviceIp, Integer deviceNum) {
        sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x84,
                (char) 0xa1,
                (char) 0x01,
                (char) 0x00,
                null
        );
    }

    public Boolean setVoltageModuleStatus(String deviceIp, Integer deviceNum, Boolean voltageModuleStatus) {
        return sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x84,
                (char) 0xa1,
                (char) 0x00,
                (char) 0x01,
                HexUtil.hexToCharArray(voltageModuleStatus ? "01" : "00")
        );
    }

    public void readGmModuleStatus(String deviceIp, Integer deviceNum) {
        sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x84,
                (char) 0xa2,
                (char) 0x01,
                (char) 0x00,
                null
        );
    }

    public Boolean setGmModuleStatus(String deviceIp, Integer deviceNum, Boolean gmModuleStatus) {
        return sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x84,
                (char) 0xa2,
                (char) 0x00,
                (char) 0x01,
                HexUtil.hexToCharArray(gmModuleStatus ? "01" : "00")
        );
    }

    public Boolean readRuningParam(String deviceIp, Integer deviceNum) {
        return sendRequestFrameMsg(
                deviceIp,
                deviceNum,
                (char) 0x83,
                (char) 0xa0,
                (char) 0x01,
                (char) 0x00,
                null
        );
    }

    private synchronized Boolean sendRequestFrameMsg(String deviceIp, Integer deviceNum, char commandType, char commandWord, char requestType, char len, char[] data) {
        if (!nettyTcpClient.connect(deviceIp)) {
            return false;
        }

        RequestFrame requestFrame = new RequestFrame();
        requestFrame.setDeviceNum(deviceNum);
        requestFrame.setCommandType(commandType);
        requestFrame.setCommandWord(commandWord);
        requestFrame.setRequestType(requestType);
        requestFrame.setLen(len);
        if (data != null) {
            requestFrame.setData(data);
        }

        nettyTcpClient.sendMsg(requestFrame.createRequestFrameString());
        return true;
    }
}
