package org.starlight.netty;

import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureJdk {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService service = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        Future<Integer> future = service.submit((new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("calculating...");
                Thread.sleep(1000);
                return 10;
            }
        }));
        log.info("waiting for result");
        log.info("the result is {}", future.get());
    }
}
