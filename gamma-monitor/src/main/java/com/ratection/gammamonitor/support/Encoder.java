package com.ratection.gammamonitor.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.Charset;
import java.util.List;

import static io.netty.buffer.Unpooled.directBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;

public class Encoder extends MessageToMessageEncoder<CharSequence> {

    private final Charset charset;

    public Encoder() {
        this(Charset.defaultCharset());
    }


    public Encoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) HexUtil.hexToInt(subStr);
        }
        return bytes;
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, CharSequence charSequence, List<Object> list) {
        if (charSequence.length() != 0) {

            list.add(unreleasableBuffer(directBuffer(charSequence.length()).writeBytes(toBytes(charSequence.toString()))));
        }
    }
}
