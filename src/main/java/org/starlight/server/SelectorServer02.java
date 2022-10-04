package org.starlight.server;

import static org.starlight.util.ByteBufferPrinter.debugRead;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;

/**
 * Selector 处理Accept
 */
@Slf4j
public class SelectorServer02 {
    public static void main(String[] args) throws IOException {

        // 1、创建Selector，管理多个Channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2、建立Selector 和Channel的联系，注册。SelectorKey就是将来事件发生时后，通过它可以通知事件和哪个Channel的事件相关
        SelectionKey sscKey = ssc.register(selector, 0, null);


        // 事件有Accept，Connect 客户端连接建立后触发，read，write四种，
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.info("register key:{}", sscKey);

        ssc.bind(new InetSocketAddress(8900));

        for (; ; ) {
            //  3、Select方法，没有事件发生时，线程阻塞，有事件发生时，线程恢复运行，如果事件未处理，则会再次发生给下面的Channel进行处理。如果确实不需要处理，则可以调用channel.cancel();
            selector.select();
            //4、处理事件，SelectionKeys中包含了所有的事件
            int selectedKeys = selector.selectedKeys().size();
            log.info("selectedKeys:{}", selectedKeys);

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                log.info("key:{}", key);
                // 处理key时，需要从SelectedKeys上删除调，否则下次就有问题
                it.remove();

                // 5、区分事件类型
                if (key.isAcceptable()) {
                    // key.cancel();
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.info("{}", sc);
                    log.info("{}", scKey);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                        int len = channel.read(byteBuffer);
                        log.info("read:{}", len);
                        if (len == -1) {
                            key.cancel();
                        } else {
                            byteBuffer.flip();
                            debugRead(byteBuffer);
                            byteBuffer.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("client disconnected, cancel the channel.");
                        key.cancel();
                    }
                }
            }
        }
    }
}
