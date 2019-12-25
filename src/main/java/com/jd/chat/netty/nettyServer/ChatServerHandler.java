package com.jd.chat.netty.nettyServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: zhilei.wang
 * @Date: 2019/12/23 16:24
 * @Version 1.0
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {


    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    // 必须是static
    public static Set<Channel> channels = new HashSet<Channel>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {

        // 接收客户端发来的消息，同时发送给其他的客户端
        Channel selfChannel = ctx.channel();
        //ChannelId id = selfChannel.id();

        channels.add(selfChannel);
        logger.warn("接收到信息: " + s);
        // 以广播的方式发送信息给其他的客户端
        for (Channel channel1 : channels) {
            if (channel1 != selfChannel) {
                // Unpooled.copiedBuffer(s, CharsetUtil.UTF_8)
                channel1.writeAndFlush(s);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.add(channel);
        String ip = channel.remoteAddress().toString();
        logger.info(ip + " :上线");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.remove(channel); //  去除此Channel
        String ip = channel.remoteAddress().toString();
        logger.info(ip + " : 离线");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        Channel channel = ctx.channel();
        ChannelFuture sync = channel.closeFuture().sync();
    }
}
