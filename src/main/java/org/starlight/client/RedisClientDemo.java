package org.starlight.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 模拟 redis 协议demo
 */
@Slf4j
public class RedisClientDemo {
    private static final byte[] LINE = new byte[]{13, 10};

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(worker);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ByteBuf buf = ctx.alloc().buffer();
                        buf.writeBytes("*3".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("$3".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("set".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("$4".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("name".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("$8".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("zhangsan".getBytes());
                        buf.writeBytes(LINE);

                        ctx.writeAndFlush(buf);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        log.info(buf.toString(Charset.defaultCharset()));
                    }
                });
            }
        }).connect(new InetSocketAddress("localhost", 6379));
    }
}
