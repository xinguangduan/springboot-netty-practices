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
public class WriteServer02 {
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
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    // 定义buffer
                    ByteBuffer buffer = Charset.defaultCharset().encode(str.toString());

                    // 返回值表示实际可以写入的字节数
                    int size = sc.write(buffer);
                    log.info("write size:{}", size);
                    // 判断是否有余量
                    if (buffer.hasRemaining()) {
                        // 关注可写事件
                        scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        // 把未写完的数据存储到scKey上
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int len = sc.write(buffer);
                    log.info("write size:{}", len);
                    // 清理操作
                    if (buffer.hasRemaining() == false) {
                        key.attach(null);// 清除buffer
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);// 不需要关注可写事件
                    }
                }
            }
        }
    }
}
