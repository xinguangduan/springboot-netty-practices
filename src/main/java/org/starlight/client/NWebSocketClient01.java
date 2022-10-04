package org.starlight.client;

import static org.asynchttpclient.Dsl.asyncHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

@Slf4j
public class NWebSocketClient01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, TimeoutException {
        Thread.sleep(10000);

        String reqString = "";

        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
                .setHandshakeTimeout(5000)
                .setConnectTimeout(10000)
                .build();
        AsyncHttpClient asyncHttpClient = asyncHttpClient(config);
        for (int i = 0; i < 10000; i++) {
            WebSocket websocket;
            int finalI = i;
            WebSocketUpgradeHandler upgradeHandler = new WebSocketUpgradeHandler.Builder().addWebSocketListener(
                    new WebSocketListener() {

                        @Override
                        public void onOpen(WebSocket websocket) {
                            log.info("received onOpen {}", finalI);
                            websocket.sendTextFrame(reqString);
                        }

                        @Override
                        public void onClose(WebSocket webSocket, int j, String s) {
                            log.info("received onClose,{}, {}, {}", j, s);
                            try {
                                webSocket.sendCloseFrame();
                                log.info("close this client,{}", webSocket);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                            log.info(payload);
                        }

                        @Override
                        public void onError(Throwable t) {
                            log.info("received onError,{}", t);
                        }
                    }).build();
            websocket = asyncHttpClient
                    .prepareGet("ws://192.168.1.3:1024/channel")
                    .execute(upgradeHandler).get(5, TimeUnit.SECONDS);

            log.info("{}===={}", websocket.getLocalAddress().toString(), websocket.getRemoteAddress().toString());
        }
        Thread.sleep(10000);
        asyncHttpClient.close();
    }
//public static void main(String[] args) throws IOException {
//    try (AsyncHttpClient asyncHttpClient = asyncHttpClient()) {
//        asyncHttpClient
//                .prepareGet("http://www.example.com/")
//                .execute()
//                .toCompletableFuture()
//                .thenApply(Response::getResponseBody)
//                .thenAccept(System.out::println)
//                .join();
//    }
//}
}
