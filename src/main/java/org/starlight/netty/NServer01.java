package org.starlight.netty;

import java.util.concurrent.TimeUnit;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NServer01 {
    public static void main(String[] args) {
        log.info("{}", NettyRuntime.availableProcessors());
        EventLoopGroup group = new NioEventLoopGroup(2);// IO事件，普通任务，定时任务
        EventLoopGroup group1 = new DefaultEventLoop();// 普通任务、定时任务

        log.info("{}", group.next());
        log.info("{}", group.next());
        log.info("{}", group.next());
        log.info("{}", group.next());

        group.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("normal task..");
        });
        group.scheduleAtFixedRate(() -> {
            log.info("schedule task");
        }, 0, 1, TimeUnit.SECONDS);
        log.info("main thread.");
    }
}
