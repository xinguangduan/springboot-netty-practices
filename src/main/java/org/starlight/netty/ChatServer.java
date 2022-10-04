package org.starlight.netty;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    //维护所有channel，key=名称，value为channel对象
    private static Map<String, NioSocketChannel> sessions=new ConcurrentHashMap<>();
    public static Map<String, NioSocketChannel> getSessions() {
        return sessions;
    }
    public static void putSession(String name,NioSocketChannel channel){
        sessions.put(name,channel);
    }

    public static void removeSession(String name){
        sessions.remove(name);
    }

    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup(6);
        new ServerBootstrap()
                .group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

//                      ch.pipeline().addLast(new LineBasedFrameDecoder(1024));//配置行解码器

                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                            }
                            //读消息
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                ByteBuf byteBuf=(ByteBuf)msg;
                                byte b[]=new byte[byteBuf.readableBytes()];
                                byteBuf.readBytes(b);
                                String str=new String(b,"utf8");
                                JSONObject jsonObject = JSON.parseObject(str);
                                int state = (int) jsonObject.get("state");
                                String username = (String) jsonObject.get("username");
                                switch (state)
                                {
                                    case 0: //上线
                                        ChatServer.putSession(username, ch);
                                        if(username.equals("client4")){ //如果是client4用户登录则群发
                                            sessions.forEach((k,v)->{

                                                ByteBuf buffer = ctx.alloc().buffer(16);
                                                try {
                                                    buffer.writeBytes("群发hhhh".getBytes("utf8"));
                                                    v.writeAndFlush(buffer);
//                                                      buffer.clear();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                            });

                                        }
                                        System.out.println("当前在线人数："+ChatServer.getSessions().size());
                                        break;
                                    case 1: //下线
                                        ChatServer.removeSession(username);
                                        ChatServer.getSessions().forEach((k,v)->{
                                            System.out.println(k);
                                            System.out.println(v.hashCode());
                                        });
                                        System.out.println("当前在线人数："+ChatServer.getSessions().size());
                                        break;
                                    default:
                                        break;
                                }
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                }).bind(9080);
    }
}
