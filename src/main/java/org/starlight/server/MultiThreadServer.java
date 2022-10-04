package org.starlight.server;

import static org.starlight.util.ByteBufferPrinter.debugAll;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8900));
        //  创建给定数量的worker池(线程池)
        int maxCoreNum = Runtime.getRuntime().availableProcessors();// 有坑，在docker中，会错估了cpu数，在jdk10之后才会正常。
        Worker[] workers = new Worker[maxCoreNum];
        log.info("start multi thread server...");
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
            log.debug("create worker {}", workers[i].name);
        }
        log.debug("waiting for client...");
        AtomicInteger next = new AtomicInteger(0);
        while (true) {
            boss.select();
            Iterator<SelectionKey> it = boss.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.info("connected... {}", sc.getRemoteAddress());
                    // 2.关联Selector
                    log.debug("before register... {}", sc.getRemoteAddress());
                    // round robin 轮询调度
                    int index = next.getAndIncrement() % workers.length;
                    workers[index].register(sc);//boss 调用，初始化selector
                    log.debug("after register... {}", sc.getRemoteAddress());
                }
            }
        }
    }

    // worker
    @Slf4j
    static class Worker implements Runnable {

        private String name;
        private Selector selector;
        private Thread thread;
        private volatile boolean start;
        private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<>();

        Worker(String name) {
            this.name = name;
        }

        // 初始化线程和selector
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup();// 唤醒select()任务
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();// 指向了 sc.register(selector, SelectionKey.OP_READ, null);
                    }
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            int len = channel.read(buffer);
                            log.info("read:{},{}", len, channel.getRemoteAddress());
                            if (len == -1) {
                                key.cancel();
                            } else {
                                buffer.flip();
                                debugAll(buffer);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
