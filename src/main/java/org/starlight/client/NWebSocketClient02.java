package org.starlight.client;

import static org.asynchttpclient.Dsl.asyncHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class NWebSocketClient02 {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, TimeoutException {
        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setHandshakeTimeout(3000)
                .setConnectTimeout(5000).build();
        AsyncHttpClient asyncHttpClient = asyncHttpClient(config);
        WebSocketUpgradeHandler upgradeHandler = buildWebSocketUpgradeHandler(asyncHttpClient);
        asyncHttpClient
                .prepareGet("ws://192.168.1.3:1024/channel")
                .execute(upgradeHandler).addListener(() -> log.info("completed..."), null);

        // log.info("{}===={}", websocket.getLocalAddress().toString(), websocket.getRemoteAddress().toString());
        log.info("async...........");
//        System.in.read();
    }

    @NotNull
    private static WebSocketUpgradeHandler buildWebSocketUpgradeHandler(AsyncHttpClient asyncHttpClient) {
        WebSocketUpgradeHandler upgradeHandler = new WebSocketUpgradeHandler.Builder().addWebSocketListener(
                new WebSocketListener() {

                    @Override
                    public void onOpen(WebSocket websocket) {
                        log.info("onOpen");
                        websocket.sendTextFrame("...");
                    }

                    @Override
                    public void onClose(WebSocket webSocket, int i, String s) {
                        log.info("onClose,{},{}", i, s);
                        try {
                            asyncHttpClient.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                        System.out.println(payload);
                    }

                    @Override
                    public void onPingFrame(byte[] payload) {
                        WebSocketListener.super.onPingFrame(payload);
                        log.info("onPingFrame,{}", payload.length);
                    }

                    @Override
                    public void onPongFrame(byte[] payload) {
                        WebSocketListener.super.onPongFrame(payload);
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.info("onError,", t);
                    }

                    @Override
                    public void onBinaryFrame(byte[] payload, boolean finalFragment, int rsv) {

                    }
                }).build();
        return upgradeHandler;
    }
}
