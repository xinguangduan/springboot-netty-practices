package org.starlight.client;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RejectedExecutionHandlerTest {
    public static void main(String[] args) throws InterruptedException {
        // 1.创建自定义线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


        // 2.创建线程任务
        for (int i = 1; i <= 6; i++) {

            // 3.执行任务
            System.out.println("执行第" + i + "个任务");
            threadPoolExecutor.execute(new runnable("任务" + i));

            System.out.println("当前核心线程数" + threadPoolExecutor.getCorePoolSize());
            System.out.println("当前线程池线程数" + threadPoolExecutor.getPoolSize());
            // 4.迭代器获取等待队列
            Iterator iterator = threadPoolExecutor.getQueue().iterator();
            System.out.print("当前等待队列 ");
            while (iterator.hasNext()) {
                runnable thread = (runnable) iterator.next();
                System.out.print(thread.name + "\t");
            }
            System.out.print("\n");
            System.out.println("--------");
        }

        Thread.sleep(10000);
        System.out.println("----休眠10秒后----");
        System.out.println("当前核心线程数" + threadPoolExecutor.getCorePoolSize());
        System.out.println("当前线程池线程数" + threadPoolExecutor.getPoolSize());
        System.out.println("当前队列任务数" + threadPoolExecutor.getQueue().size());

        // 5.关闭线程池
        threadPoolExecutor.shutdown();
    }

    // 实现Runnable
    static class runnable implements Runnable {
        // 设置任务名
        String name;

        public runnable(String setName) {
            this.name = setName;
        }

        @Override
        public void run() {
            try {
                System.out.println("线程:" + Thread.currentThread().getName() + " 执行: " + name);
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
