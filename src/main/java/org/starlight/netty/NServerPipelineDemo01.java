package org.starlight.netty;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NServerPipelineDemo01 {
    public static void main(String[] args) {
        log.info("start server...");
        new ServerBootstrap().group(new NioEventLoopGroup()).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {

                // head-> h1 ->h2 ->3 ->tail
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast("h1", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("h1");
                        ByteBuf buf = (ByteBuf) msg;
                        String name = buf.toString(Charset.defaultCharset());
                        super.channelRead(ctx, name);
                    }
                });

                pipeline.addLast("r2", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("h1");
                        Student student = Student.builder().name(msg.toString()).build();
                        super.channelRead(ctx, student);
                    }
                });

                pipeline.addLast("w1", new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("w1");
                        super.write(ctx, msg, promise);
                    }
                });
                pipeline.addLast("h3", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("h3,{},{}", msg, msg.getClass());
                        Student s = (Student) msg;
                        log.info("h3,{}", s);
                        super.channelRead(ctx, msg);
                        //ch.writeAndFlush("hello");
//                        ch.writeAndFlush(ctx.alloc().buffer().writeBytes("hello".getBytes(StandardCharsets.UTF_8)));
                        ctx.writeAndFlush("write hello ");
                    }
                });

                pipeline.addLast("w2", new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("w2");
                        super.write(ctx, msg, promise);
                    }
                });
                pipeline.addLast("w3", new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("w3");
                        super.write(ctx, msg, promise);
                    }
                });
            }
        }).bind(9929);
    }
}
