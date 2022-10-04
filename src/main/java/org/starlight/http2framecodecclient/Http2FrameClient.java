/*
 * Copyright 2022 learn-netty4 Project
 *
 * The learn-netty4 Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.starlight.http2framecodecclient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用Http2FrameCodec发送HTTP2 frame的http2 client
 */
@Slf4j
public final class Http2FrameClient {

    static final boolean SSL = true;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8000"));
    static final String PATH = System.getProperty("path", "/events");

    private Http2FrameClient() {
    }

    public static void main(String[] args) throws Exception {
        final EventLoopGroup clientWorkerGroup = new NioEventLoopGroup();

        // SSL配置
        final SslContext sslCtx;
        if (SSL) {
            SslProvider provider =
                    SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
            sslCtx = SslContextBuilder.forClient()
                    .sslProvider(provider)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    // 因为我们的证书是自生成的，所以需要信任放行
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .applicationProtocolConfig(new ApplicationProtocolConfig(
                            Protocol.ALPN,
                            SelectorFailureBehavior.NO_ADVERTISE,
                            SelectedListenerFailureBehavior.ACCEPT,
                            ApplicationProtocolNames.HTTP_2,
                            ApplicationProtocolNames.HTTP_1_1))
                    .build();
        } else {
            sslCtx = null;
        }

        try {
            final Bootstrap b = new Bootstrap();
            b.group(clientWorkerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.remoteAddress(HOST, PORT);
            b.handler(new Http2ClientFrameInitializer(sslCtx));

            // 启动客户端
            final Channel channel = b.connect().syncUninterruptibly().channel();
            log.info("连接到 [" + HOST + ':' + PORT + ']');

            //从当前channel中，新建一个streamChannel，发送请求消息
            final Http2ClientStreamFrameHandler streamFrameResponseHandler =
                    new Http2ClientStreamFrameHandler();

            final Http2StreamChannelBootstrap streamChannelBootstrap = new Http2StreamChannelBootstrap(channel);
            final Http2StreamChannel streamChannel = streamChannelBootstrap.open().syncUninterruptibly().getNow();
            streamChannel.pipeline().addLast(streamFrameResponseHandler);

            // 发送HTTP2 get请求
//            final DefaultHttp2Headers headers = new DefaultHttp2Headers();
//            headers.method("GET");
//            headers.path(PATH);
//            headers.scheme(SSL? "https" : "http");
//            Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(headers, true);
//            streamChannel.writeAndFlush(headersFrame);
//            log.info("发送 HTTP/2 GET 请求 " + PATH);
            String token = "eyQ_dy91EAkIxy7WTB_29Qi_9IK1Xok1hgkYRv_Ox7VDUP7YskWczlB9AFqhIvNR.1sVBWNKKRNA5cotgBG9ULA";
            final DefaultHttp2Headers headers = new DefaultHttp2Headers();
            headers.method("POST");
            headers.path(PATH);
            headers.scheme(SSL ? "https" : "http");
            headers.add("authorization", token);
            Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(headers, true);

            streamChannel.writeAndFlush(headersFrame);

            DefaultHttp2DataFrame dataFrame =new DefaultHttp2DataFrame( ByteBufAllocator.DEFAULT.buffer().writeBytes("1234578hello".getBytes(StandardCharsets.UTF_8)),true);

            log.info("发送 HTTP/2 POST 请求 " + PATH);

            // 等待响应结束或者超时
            if (!streamFrameResponseHandler.responseSuccessfullyCompleted()) {
                log.info("在5s之内没有收到系统响应.");
            }

            log.info("HTTP2请求结束，关闭连接.");

            // 关闭连接
            channel.close().syncUninterruptibly();
        } finally {
            clientWorkerGroup.shutdownGracefully();
        }
    }

}
