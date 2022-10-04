package org.starlight.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteServer01 {
    public static void main(String[] args) throws IOException {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 3000000; i++) {
            str.append("a");
        }
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8900));
        log.info("write server start...");
        for (; ; ) {
            selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = Charset.defaultCharset().encode(str.toString());
                    // 向客户端写
                    while (buffer.hasRemaining()) {
                        int size = sc.write(buffer);
                        log.info("write size:{}", size);
                    }
                }
            }
        }
    }
}
