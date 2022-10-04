package org.starlight.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NServer00 {
    public static void main(String[] args) {
        log.info("start server...");
        // 1 启动器，负责包装netty组件，启动服务器
        new ServerBootstrap()
                .group(new NioEventLoopGroup()) // 2. BossEventLoop，WorkerEventLoop (selector, thread)
                .channel(NioServerSocketChannel.class)// 3、选择服务器的 ServerSocketChannel 实现
                .childHandler(new ChannelInitializer<NioSocketChannel>() { // 4、Boss 负责处理连接 worker负责处理读写，决定了worker能执行那些操作(handler）
                    @Override
                    protected void initChannel(NioSocketChannel ch) { // 添加具体Channel
                       log.info("connection init...");
                        // 将ByteBuf 转换为字符串
                        ch.pipeline().addLast(new StringDecoder());
                        //  读事件处理
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {// 打印上一步转换好的字符串
                                log.info("{}", msg);
                            }
                        });
                    }
                })
                .bind(8921);
    }
}
