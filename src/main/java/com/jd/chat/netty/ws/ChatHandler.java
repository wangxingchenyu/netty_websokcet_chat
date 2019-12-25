package com.jd.chat.netty.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

/**
 * @Author: zhilei.wang
 * @Date: 2019/12/25 9:21
 * @Version 1.0
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //public static Set<Channel> channels = new HashSet<>();
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        Channel inChannel = ctx.channel(); // 当前的通道

        // 执行广播
        for (Channel channel : channels) {
            if (channel != inChannel) {
                // 发送给其他人
                channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress() + " :"+content));
            } else {
                // 发送给自己
                channel.writeAndFlush(new TextWebSocketFrame("我 : " + content));
            }
        }

    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel inChannel = ctx.channel();
        for (Channel channel : channels) {
            if (channel != inChannel){
                channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress() + ": 进入聊天室"));
            }
        }
        channels.add(ctx.channel());
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress() + ": 离开聊天室"));
        }
        channels.remove(ctx.channel());
    }
}
