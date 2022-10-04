package org.starlight.server;

import java.io.IOException;
import java.net.InetSocketAddress;
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
public class SelectorServer01 {
    public static void main(String[] args) throws IOException {

        // 1、创建Selector，管理多个Channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2、建立Selector 和Channel的联系，注册。SelectorKey就是将来事件发生时后，通过它可以通知事件和哪个Channel的事件相关
        SelectionKey selectionKey = ssc.register(selector, 0, null);


        // 事件有Accept，Connect 客户端连接建立后触发，read，write四种，
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        log.info("register key:{}", selectionKey);

        ssc.bind(new InetSocketAddress(8900));

        for (; ; ) {
            //  3、Select方法，没有事件发生时，线程阻塞，有事件发生时，线程恢复运行，如果事件未处理，则会再次发生给下面的Channel进行处理。如果确实不需要处理，则可以调用channel.cancel();
            selector.select();
            //4、处理事件，SelectionKeys中包含了所有的事件
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                // key.cancel();
                log.info("key:{}", key);
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel sc = channel.accept();
                log.info("{}", sc);
            }
        }
    }
}
