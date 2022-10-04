package org.starlight.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NServer03 {
    public static void main(String[] args) {
        EventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup();
        new ServerBootstrap()
                // 细分：boss负责ServerSocketChannel上的Accept事件，worker只负责SocketChannel上的读写
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        log.info("connection init...");
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                ByteBuf buf = (ByteBuf) msg;
                                log.info("handle1 read msg {},{}", ctx.channel().id(), msg);
                                ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast(defaultEventLoopGroup, "handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                ByteBuf buf = (ByteBuf) msg;
                                log.info("default group execute long task {},{}", ctx.channel().id(), msg.toString());
                            }
                        });
                    }
                })
                .bind(9919);
    }
}
