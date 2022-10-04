package org.starlight.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileChannelDemo00 {

    public static void main(String[] args) {
        String fromFilePath = "../../from.txt";
        String toFilePath = "../../to.txt";
        log.info("start...");
        try (
                FileChannel from = new FileInputStream(fromFilePath).getChannel();
                FileChannel to = new FileOutputStream(toFilePath).getChannel();
        ) {
            // 效率高，底层会利用操作系统的零拷贝进行优化，2G大小
            long size = from.size();
            for (long left = size; left > 0; ) {
                log.info("copy data,left:{}", left);
                left -= from.transferTo((size - left), left, to);
            }
            log.info("finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
