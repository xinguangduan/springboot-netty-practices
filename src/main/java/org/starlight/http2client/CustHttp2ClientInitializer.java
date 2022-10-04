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
package org.starlight.http2client;

import static io.netty.handler.logging.LogLevel.INFO;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpClientUpgradeHandler;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 初始化client,让其支持http2
 */
@Slf4j
public class CustHttp2ClientInitializer extends ChannelInitializer<SocketChannel> {
    private static final Http2FrameLogger logger = new Http2FrameLogger(INFO, CustHttp2ClientInitializer.class);

    private final SslContext sslCtx;
    private final int maxContentLength;
    private HttpToHttp2ConnectionHandler connectionHandler;
    private CustHttpResponseHandler responseHandler;
    private CustHttp2SettingsHandler settingsHandler;

    public CustHttp2ClientInitializer(SslContext sslCtx, int maxContentLength) {
        this.sslCtx = sslCtx;
        this.maxContentLength = maxContentLength;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        Http2Connection connection = new DefaultHttp2Connection(false);
        connectionHandler = new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(new DelegatingDecompressorFrameListener(
                        connection,
                        new InboundHttp2ToHttpAdapterBuilder(connection)
                                .maxContentLength(maxContentLength)
                                .propagateSettings(true)
                                .build()))
                .frameLogger(logger)
                .connection(connection)
                .build();
        responseHandler = new CustHttpResponseHandler();
        settingsHandler = new CustHttp2SettingsHandler(ch.newPromise());
        if (sslCtx != null) {
            configureSsl(ch);
        } else {
            configureClearText(ch);
        }
    }

    public CustHttpResponseHandler responseHandler() {
        return responseHandler;
    }

    public CustHttp2SettingsHandler settingsHandler() {
        return settingsHandler;
    }

    /**
     * 配置TLS的ProtocolNegotiationHandler
     */
    private void configureSsl(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // 指定TLS支持的host和port
        pipeline.addLast(sslCtx.newHandler(ch.alloc(), CustHttp2Client.HOST, CustHttp2Client.PORT));
        // 握手中的ProtocolNegotiation
        pipeline.addLast(new ApplicationProtocolNegotiationHandler("") {
            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    ChannelPipeline p = ctx.pipeline();
                    p.addLast(connectionHandler);
                    p.addLast(settingsHandler, responseHandler);
                    return;
                }
                ctx.close();
                throw new IllegalStateException("未知协议: " + protocol);
            }
        });
    }

    /**
     * h2c 从 HTTP 升级到 HTTP/2.
     */
    private void configureClearText(SocketChannel ch) {
        HttpClientCodec sourceCodec = new HttpClientCodec();
        Http2ClientUpgradeCodec upgradeCodec = new Http2ClientUpgradeCodec(connectionHandler);
        HttpClientUpgradeHandler upgradeHandler = new HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 65536);

        ch.pipeline().addLast(sourceCodec,
                              upgradeHandler,
                              new CustUpgradeRequestHandler(this),
                              new UserEventLogger());
    }

    /**
     * 记录用户触发的事件
     */
    private static class UserEventLogger extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            log.info("用户触发事件: " + evt);
            ctx.fireUserEventTriggered(evt);
        }
    }
}
