package com.ratection.gammamonitor;

import com.ratection.gammamonitor.client.NettyTcpClient;
import com.ratection.gammamonitor.client.dto.RequestFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableSwagger2
public class GammaMonitorApplication implements CommandLineRunner {

    @Autowired
    ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(GammaMonitorApplication.class, args);
    }

    @Override
    public void run(String... args) {

//        NettyTcpClient client = context.getBean(NettyTcpClient.class);
//        client.connect("192.168.10.5");
//        client.sendMsg(new String(new char[]{0x55, 0xaa, 0xfb, 0xfe, 0xf2, 0xdd, 0x9f, 0x3c, 0x81, 0xa0,
//                0x01, 0x00, 0xa7, 0xbf, 0x55, 0xbb, 0xfe, 0xfd}));


//        RequestFrame requestFrame = new RequestFrame();
//        requestFrame.setDeviceNum(1017110002);
//        requestFrame.setCommandType((char) 0x82);
//        requestFrame.setCommandWord((char) 0xa1);
//        requestFrame.setRequestType((char) 0x01);
//        requestFrame.setLen((char) 0x01);
//        requestFrame.setData(new char[]{0x01});
//        client.sendMsg(requestFrame.createRequestFrameString());
    }
}
