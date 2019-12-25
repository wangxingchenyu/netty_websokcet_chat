package com.jd.chat.netty.ws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Author: zhilei.wang
 * @Date: 2019/12/25 9:20
 * @Version 1.0
 */
@Component
public class ChatServerApplication {

    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;

    private Channel channel;

    public void start() throws InterruptedException {
        System.out.println("Netty成功启动.......................");
        channel = serverBootstrap.bind(8888).sync().channel().closeFuture().sync().channel();
    }

    public void close() {
        channel.close();
        channel.parent().close();
    }


}
