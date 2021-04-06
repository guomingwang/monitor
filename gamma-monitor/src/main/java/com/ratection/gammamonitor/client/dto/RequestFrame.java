package com.ratection.gammamonitor.client.dto;

import com.ratection.gammamonitor.support.Crc16Modbus;
import com.ratection.gammamonitor.support.HexUtil;
import lombok.Data;

@Data
public class RequestFrame {
    private char[] head = new char[]{0x55, 0xaa, 0xfb, 0xfe};
    private int deviceNum;
    private char commandType;
    private char commandWord;
    private char requestType;
    private char len;
    private char[] data;
    private char[] crc;
    private char[] tail = new char[]{0x55, 0xbb, 0xfe, 0xfd};

    public String createRequestFrameString() {

        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(head))
                .append(String.valueOf(HexUtil.hexToCharArray(HexUtil.intInverseToHexString(deviceNum))))
                .append(commandType)
                .append(commandWord)
                .append(requestType)
                .append(len);
        if (len != 0x00) {
            sb.append(String.valueOf(data));
        }
        sb.append(createCrc())
                .append(String.valueOf(tail));

        return sb.toString();
    }

    private char[] createCrc() {

        String originalStr = HexUtil.intInverseToHexString(deviceNum)
                + HexUtil.intToHexString(commandType)
                + HexUtil.intToHexString(commandWord)
                + HexUtil.intToHexString(requestType)
                + HexUtil.intToHexString(len);
        String crcString = Crc16Modbus.getCRC(HexUtil.hexToByteArray(originalStr));

        return HexUtil.hexToCharArray(crcString);
    }
}
