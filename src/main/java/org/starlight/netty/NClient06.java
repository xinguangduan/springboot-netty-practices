package org.starlight.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 黏包例子客户端代码
 */
@Slf4j
public class NClient06 {
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,100);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.WRITER_IDLE) {
                                log.info("Channel {} 已经3秒没有写数据", ctx.channel().id());
                                ctx.writeAndFlush("send ping");
                            }
                        }
                    });
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                            log.info("channelRegistered", ctx);
                            super.channelRegistered(ctx);
                        }

                        @Override
                        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                            log.info("channelUnregistered", ctx);
                            super.channelUnregistered(ctx);
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.info("channelActive", ctx);

                            for (int i = 0; i < 10; i++) {
                                ByteBuf buf = ctx.alloc().buffer(32);
                                buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                ctx.writeAndFlush(buf);
                            }
                            super.channelActive(ctx);
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.info("channelInactive", ctx);
                            super.channelInactive(ctx);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("channelRead", ctx);
                            super.channelRead(ctx, msg);
                        }

                        @Override
                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                            log.info("channelReadComplete", ctx);
                            super.channelReadComplete(ctx);
                        }

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            log.info("userEventTriggered", ctx);
                            super.userEventTriggered(ctx, evt);
                        }

                        @Override
                        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                            log.info("channelWritabilityChanged", ctx);
                            super.channelWritabilityChanged(ctx);
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.info("exceptionCaught", cause);
                            super.exceptionCaught(ctx, cause);
                        }
                    });
                }
            });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8999)).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
