package org.starlight.netty;

import java.util.concurrent.ExecutionException;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FuturePromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoopGroup executors = new NioEventLoopGroup();
        //  准备EventLoop
        EventLoop eventLoop = executors.next();
        //  主动创建Promise结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(() -> {
            try {
                log.info("executing ...");
                Thread.sleep(1000);
                //int i = 1 / 0;
                promise.setSuccess(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }

        }).start();
        log.info("waiting for result...");
        log.info("the result is...{}", promise.get());
    }
}
