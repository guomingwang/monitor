package com.ratection.gammamonitor.client.dto;

import lombok.Data;

@Data
public class ResponseFrame {
    private String head;
    private int deviceNum;
    private String commandType;
    private String commandWord;
    private String responseType;
    private String responseStatus;
    private String len;
    private String data;
    private String crc;
    private String tail;
}
