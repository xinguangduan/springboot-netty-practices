package org.starlight;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufferDemo04 {

    public static void main(String[] args) {
        final String filePath = "../data.txt";
        //  1、 输入输出流，2、RandomAccessFile
        try (FileChannel channel = new FileInputStream(filePath).getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            for (; ; ) {
                // 从channel中读取数据，向buffer中写入
                int len = channel.read(buffer);
                log.info("recv len:{}", len);
                if (len == -1) {// 读取结束
                    log.info("read EOF");
                    break;
                }
                buffer.flip();//切换为读模式

                while (buffer.hasRemaining()) {//循环读，并判断是否还有数据
                    byte b = buffer.get();
                    log.info("byte:{}", (char) b);
                }
                buffer.clear();//切换为写模式
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
