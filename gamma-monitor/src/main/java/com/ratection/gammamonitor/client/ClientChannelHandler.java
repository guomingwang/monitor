package com.ratection.gammamonitor.client;

import com.ratection.gammamonitor.client.dto.ResponseFrame;
import com.ratection.gammamonitor.datasource.InfluxDataSource;
import com.ratection.gammamonitor.model.Setting;
import com.ratection.gammamonitor.service.SettingService;
import com.ratection.gammamonitor.support.Crc16Modbus;
import com.ratection.gammamonitor.support.HexUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDBIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Sharable
@Slf4j
public class ClientChannelHandler extends SimpleChannelInboundHandler<ResponseFrame> {

    private SettingService settingService;
    private InfluxDataSource influxDataSource;

    @Value("${influxdb.measurement}")
    private String measurement;

    @Autowired
    public ClientChannelHandler(SettingService settingService, InfluxDataSource influxDataSource) {
        this.settingService = settingService;
        this.influxDataSource = influxDataSource;
    }

    /**
     * 从服务器接收到的msg
     *
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseFrame msg) {
        if (msg.getHead() == null || !msg.getHead().toUpperCase().equals("55AAFBFE")) {
            log.error("response frame head error:{}", msg.getHead());
            ctx.close();
            return;
        }
        if (!createCrc(msg).equals(msg.getCrc())) {
            log.info("Netty tcp client receive msg : {}", msg);
            log.error("response frame crc check failed.");
            ctx.close();
            return;
        }
        if (msg.getTail() == null || !msg.getTail().toUpperCase().equals("55BBFEFD")) {
            log.error("response frame tail error:{}", msg.getHead());
            ctx.close();
            return;
        }
        if (msg.getLen().equals("01") && msg.getData().equals("01")) {
            ctx.close();
            return;
        }

        try {
            switch (msg.getCommandType()) {
                case "81":
                    deviceInformation();
                    break;
                case "82":
                    netParameter();
                    break;
                case "83":
                    sensorParamter(msg);
                    break;
                case "84":
                    deviceController(msg);
                    break;
            }
        } catch (Exception e) {
            log.error("something is wrong: ", e);
        } finally {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            log.error("caught exception: ", cause);
            ctx.fireExceptionCaught(cause);
            channel.close();
        } else {
            log.error(channel.remoteAddress() + "断开了连接");
            cause.printStackTrace();
        }
        ctx.close();
    }

    private void deviceInformation(){}

    private void netParameter(){}

    private void sensorParamter(ResponseFrame responseFrame) {
        if (responseFrame.getCommandWord().toUpperCase().equals("A0") && responseFrame.getResponseType().toUpperCase().equals("D8")) {
            String[] dataHex = new String[12];
            dataHex[0] = responseFrame.getData().substring(0, 8);
            dataHex[1] = responseFrame.getData().substring(8, 16);
            dataHex[2] = responseFrame.getData().substring(16, 24);
            dataHex[3] = responseFrame.getData().substring(24, 32);
            dataHex[4] = responseFrame.getData().substring(32, 40);
            dataHex[5] = responseFrame.getData().substring(40, 48);
            dataHex[6] = responseFrame.getData().substring(48, 56);
            dataHex[7] = responseFrame.getData().substring(56, 64);
            dataHex[8] = responseFrame.getData().substring(64, 72);
            dataHex[9] = responseFrame.getData().substring(72, 74);
            dataHex[10] = responseFrame.getData().substring(74, 76);
            dataHex[11] = responseFrame.getData().substring(76, 84);

            float value = HexUtil.hexInverseToFloat(dataHex[4]);
            float count = HexUtil.hexInverseToFloat(dataHex[5]);
            float temperature = HexUtil.hexInverseToFloat(dataHex[6]);
            float humidity = HexUtil.hexInverseToFloat(dataHex[7]);
            float voltage = HexUtil.hexInverseToFloat(dataHex[8]);
            String level = HexUtil.hexToInt(dataHex[9]) == 0 ? "Low" : "High";
            int deviceNum = HexUtil.hexToInt(HexUtil.inverseHex(dataHex[11]));

            Map<String, String> tags = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            tags.put("device-num", String.valueOf(deviceNum));
            Setting setting = settingService.getByDeviceNum(String.valueOf(deviceNum));
            if (setting == null) {
                return;
            }
            tags.put("device-name", setting.getDeviceName());
            tags.put("system", setting.getSystem());
            tags.put("level", level);

            fields.put("count", Integer.valueOf(new DecimalFormat("#0").format(count)));
            fields.put("temperature", Double.valueOf(new DecimalFormat("#0.0").format(temperature)));
            fields.put("humidity", Double.valueOf(new DecimalFormat("#0.0").format(humidity)));
            fields.put("voltage", Integer.valueOf(new DecimalFormat("#0").format(voltage)));
            fields.put("gamma-value", Double.valueOf(new DecimalFormat("#0.000").format(value)));

            //store data to influxDb
            influxDataSource.insert(measurement, tags, fields, new Date().getTime(), TimeUnit.MILLISECONDS);
        }

        if (responseFrame.getCommandWord().toUpperCase().equals("A6") && responseFrame.getResponseType().toUpperCase().equals("D8")) {
            settingService.updateAlarmThreshold(String.valueOf(responseFrame.getDeviceNum()), HexUtil.hexInverseToFloat(responseFrame.getData()));
        }
    }

    private void deviceController(ResponseFrame responseFrame){
        if (responseFrame.getCommandWord().toUpperCase().equals("A1") && responseFrame.getResponseType().toUpperCase().equals("D8")) {
            settingService.updateVoltageModuleStatus(String.valueOf(responseFrame.getDeviceNum()), HexUtil.hexToInt(responseFrame.getData()) != 0);
        }
        if (responseFrame.getCommandWord().toUpperCase().equals("A2") && responseFrame.getResponseType().toUpperCase().equals("D8")) {
            settingService.updateGmModuleStatus(String.valueOf(responseFrame.getDeviceNum()), HexUtil.hexToInt(responseFrame.getData()) != 0);
        }
    }

    private String createCrc(ResponseFrame responseFrame) {

        String originalStr = HexUtil.intInverseToHexString(responseFrame.getDeviceNum())
                + responseFrame.getCommandType() + responseFrame.getCommandWord()
                + responseFrame.getResponseType() + responseFrame.getResponseStatus()
                + responseFrame.getLen() + responseFrame.getData();
        String res = Crc16Modbus.getCRC(HexUtil.hexToByteArray(originalStr));

        return res.length() == 2 ? "00" + res : res;
    }
}
