package org.starlight.netty;

import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient01 {
    public static void main(String[] args) {
        String name = "client1";
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            new Bootstrap()
                    .group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    User user = new User();
                                    user.setUsername(name);
                                    user.setState(0);
                                    String jsonString = JSON.toJSONString(user);
                                    ByteBuf buffer = ctx.alloc().buffer(16);
                                    buffer.writeBytes(jsonString.getBytes("utf8"));
                                    ch.writeAndFlush(buffer);
                                    super.channelActive(ctx);
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    User user = new User();
                                    user.setUsername(name);
                                    user.setState(1);
                                    String jsonString = JSON.toJSONString(user);
                                    ByteBuf buffer = ctx.alloc().buffer(16);
                                    buffer.writeBytes(jsonString.getBytes("utf8"));
                                    ch.writeAndFlush(buffer);
                                    super.channelInactive(ctx);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(msg);
                                    super.channelRead(ctx, msg);
                                }
                            });
                        }
                    }).connect("localhost", 9080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
