package org.starlight.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteClient01 {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        // sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 8900));
        int readCount = 0;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int size = sc.read(buffer);
            if (size == -1) {
                break;
            }
            readCount += size;
            log.info("readCount:{}", readCount);
            buffer.clear();
        }
    }
}
