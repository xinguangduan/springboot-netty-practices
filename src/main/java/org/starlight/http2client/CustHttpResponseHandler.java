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

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理 FullHttpResponse
 */
@Slf4j
public class CustHttpResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final Map<Integer, Entry<ChannelFuture, ChannelPromise>> streamidPromiseMap;

    public CustHttpResponseHandler() {
        streamidPromiseMap = PlatformDependent.newConcurrentHashMap();
    }

    public void put(int streamId, ChannelFuture writeFuture, ChannelPromise promise) {
        streamidPromiseMap.put(streamId, new SimpleEntry<>(writeFuture, promise));
    }

    /**
     * 等待响应
     */
    public void awaitResponses(long timeout, TimeUnit unit) {
        Iterator<Entry<Integer, Entry<ChannelFuture, ChannelPromise>>> itr = streamidPromiseMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<Integer, Entry<ChannelFuture, ChannelPromise>> entry = itr.next();
            ChannelFuture writeFuture = entry.getValue().getKey();
            if (!writeFuture.awaitUninterruptibly(timeout, unit)) {
                throw new IllegalStateException("写入时间超时 stream id " + entry.getKey());
            }
            if (!writeFuture.isSuccess()) {
                throw new RuntimeException(writeFuture.cause());
            }
            ChannelPromise promise = entry.getValue().getValue();
            if (!promise.awaitUninterruptibly(timeout, unit)) {
                throw new IllegalStateException("等待响应超时 stream id " + entry.getKey());
            }
            if (!promise.isSuccess()) {
                throw new RuntimeException(promise.cause());
            }
            log.info("接收到stream id={} 的数据",entry.getKey());
            itr.remove();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        Integer streamId = msg.headers().getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
        if (streamId == null) {
            log.info("接收到未知消息: {}" , msg);
            return;
        }

        Entry<ChannelFuture, ChannelPromise> entry = streamidPromiseMap.get(streamId);
        if (entry == null) {
            log.info("接收到未知 stream id={} 的数据",streamId);
        } else {
            // 处理消息
            ByteBuf content = msg.content();
            if (content.isReadable()) {
                int contentLength = content.readableBytes();
                byte[] arr = new byte[contentLength];
                content.readBytes(arr);
                log.info("接收到数据:{}",new String(arr, 0, contentLength, CharsetUtil.UTF_8));
            }

            entry.getValue().setSuccess();
        }
    }
}
