package org.starlight.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServer01 {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class);
            //  调整系统的接收缓冲器（滑动窗口大小）
            // bootstrap.option(ChannelOption.SO_RCVBUF,10);
            //  调整Netty的接收缓冲区（byteBuf）
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16));//  最小是16

            bootstrap.group(boss, worker);
            // 到底用NIOSocketChannel还是用SocketChannel？实践证明，这两个都行
            bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {

                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    log.info("connection init.");
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    //ch.pipeline().addLast(new StringDecoder());
//                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//                        @Override
//                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            log.info("");
//                        }
//                    });
                }
            });
            log.info("server start...");
            ChannelFuture future = bootstrap.bind(9999).sync();
            future.channel().closeFuture().sync();

        } catch (Exception ex) {
            log.error("server error", ex);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
