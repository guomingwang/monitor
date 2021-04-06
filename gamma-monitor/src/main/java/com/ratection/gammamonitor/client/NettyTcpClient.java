package com.ratection.gammamonitor.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyTcpClient {

    @Value("${netty.tcp.server.port}")
    private Integer PORT;

    @Autowired
    private ClientChannelInitializer clientChannelInitializer;

    //与服务端建立连接后得到的通道对象
    private Channel channel;

    private EventLoopGroup group = new NioEventLoopGroup();

    /**
     * 初始化 `Bootstrap` 客户端引导程序
     *
     * @return Bootstrap
     */
    private Bootstrap getBootstrap() {
        Bootstrap b = new Bootstrap();
//        group = new NioEventLoopGroup(1);
        b.group(group)
                .channel(NioSocketChannel.class) //通道连接者
                .handler(clientChannelInitializer) //通道处理者
                .option(ChannelOption.SO_KEEPALIVE, true) //心跳报活
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,1000);
        return b;
    }

    /**
     * 建立连接，获取连接通道对象
     */
    public Boolean connect(String host) {
        ChannelFuture channelFuture = getBootstrap().connect(host, PORT).awaitUninterruptibly();
        if (channelFuture != null) {
            if (channelFuture.isSuccess()) {
//                log.info("connect tcp server host = {}, port = {} success", host, PORT);
                channel = channelFuture.channel();
                return true;
            } else {
                channelFuture.channel().close();
            }
        }
        log.error("connect tcp server host = {}, port = {} fail", host, PORT);
//        group.shutdownGracefully();
        return false;
    }

    /**
     * 向服务器发送消息
     *
     * @param msg 编码后的对象
     */
    public void sendMsg(Object msg) {
        if (channel != null) {
            try {
                if (channel.isWritable()) {
                    channel.writeAndFlush(msg).sync();
                }
            } catch (InterruptedException e) {
                log.error("interrupt exception: ", e);
                channel.close();
            }
        } else {
            log.warn("消息发送失败,连接尚未建立!");
        }
    }

}

