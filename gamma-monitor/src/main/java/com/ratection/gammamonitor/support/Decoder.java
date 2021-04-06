package com.ratection.gammamonitor.support;

import com.ratection.gammamonitor.client.dto.ResponseFrame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Decoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        // 读取原始数据串 不会改变ByteBuf 索引
        String originalData = ByteBufUtil.hexDump(byteBuf);
//        log.info("original data:{}", originalData);  //减少日志输出

        ResponseFrame responseFrame = new ResponseFrame();
        responseFrame.setHead(ByteBufUtil.hexDump(byteBuf, 0, 4));

        String deviceNumHex = ByteBufUtil.hexDump(byteBuf, 4, 4);
        StringBuilder deviceNum = new StringBuilder();
        for (int i = 0; i < deviceNumHex.length() / 2; i ++) {
            deviceNum.insert(0, deviceNumHex.substring(i * 2, i * 2 + 2));
        }
        responseFrame.setDeviceNum(HexUtil.hexToInt(deviceNum.toString()));

        responseFrame.setCommandType(ByteBufUtil.hexDump(byteBuf, 8, 1));
        responseFrame.setCommandWord(ByteBufUtil.hexDump(byteBuf, 9, 1));
        responseFrame.setResponseType(ByteBufUtil.hexDump(byteBuf, 10, 1));
        responseFrame.setResponseStatus(ByteBufUtil.hexDump(byteBuf, 11, 2));
        responseFrame.setLen(ByteBufUtil.hexDump(byteBuf, 13, 1));
        int dataLength = HexUtil.hexToInt(responseFrame.getLen());
        responseFrame.setData(ByteBufUtil.hexDump(byteBuf, 14, dataLength));
        responseFrame.setCrc(ByteBufUtil.hexDump(byteBuf, 14 + dataLength, 2));
        responseFrame.setTail(ByteBufUtil.hexDump(byteBuf, 16 + dataLength, 4));

        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
//        list.add(HexUtil.bytesToHexFun1(req));

        list.add(responseFrame);
    }
}
