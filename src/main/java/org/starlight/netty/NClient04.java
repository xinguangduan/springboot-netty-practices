package org.starlight.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NClient04 {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        log.info("channel init...");
                        ch.pipeline().addLast(new StringEncoder());
                    }
                }).connect("127.0.0.1", 9919);

        // 1、通过sync实现连接
//        channelFuture.sync();
//        Channel ch = channelFuture.channel();
//        log.info("{}", ch);
//        ch.writeAndFlush("你好啊");
        // 2.  通过Listener
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("connection successful.");
                Channel ch = channelFuture.channel();
                log.info("{}", ch);
                ch.writeAndFlush("你好啊");
            }
        });
    }
}
