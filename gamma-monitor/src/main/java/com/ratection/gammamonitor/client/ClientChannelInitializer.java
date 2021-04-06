package com.ratection.gammamonitor.client;

import com.ratection.gammamonitor.support.Decoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ClientChannelHandler clientChannelHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();

        //IdleStateHandler心跳机制,如果超时触发Handle中userEventTrigger()方法
        pipeline.addLast(
                "idleStateHandler",
                new IdleStateHandler(2, 2, 2, TimeUnit.SECONDS)
        );

        //字符串编解码器
        pipeline.addLast(
                new Decoder(),
                new StringEncoder(CharsetUtil.ISO_8859_1)
        );

        //自定义Handler
        pipeline.addLast("MyOutboundHandler", new ChannelOutboundHandlerAdapter());
        pipeline.addLast("clientChannelHandler", clientChannelHandler);

    }
}
