package org.starlight.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NClient03 {
    public static void main(String[] args) throws InterruptedException {

        new Bootstrap()
                // 细分：boss负责ServerSocketChannel上的Accept事件，worker只负责SocketChannel上的读写
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        log.info("connection init...");
                        ch.pipeline().addLast(new StringEncoder());
                    }
                }).connect(new InetSocketAddress("127.0.0.1", 9919)).sync().channel().writeAndFlush("wwww");
    }
}
