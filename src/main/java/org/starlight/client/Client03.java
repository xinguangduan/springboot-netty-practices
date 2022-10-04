package org.starlight.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client03 {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8900));
        sc.write(ByteBuffer.wrap("中国".getBytes(StandardCharsets.UTF_8)));

        for (;;) {
            log.info("Health check");
            Thread.sleep(5000);
        }
    }
}
