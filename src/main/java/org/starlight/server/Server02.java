package org.starlight.server;

import static org.starlight.util.ByteBufferPrinter.debugRead;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 非阻塞模式,在没有请求时也在空转，浪费资源，不可用于生产
 */
@Slf4j
public class Server02 {
    static final List<SocketChannel> channels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //  非阻塞模式
        ssc.configureBlocking(false);
        // 2 绑定监听端口
        ssc.bind(new InetSocketAddress(8900));
        // 3\接收连接
        while (true) {
            //log.info("waiting for connection...");
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                log.info("connected.");
                sc.configureBlocking(false);
                add(sc);
            }

            for (SocketChannel socketChannel : channels) {
               // log.info("before read {}", socketChannel);
                int size = socketChannel.read(buffer);
                if (size > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.info("after read {}", socketChannel);
                }
            }
        }
    }

    private static void add(SocketChannel channel) {
        channels.add(channel);
        log.info("add new client");
    }
}
