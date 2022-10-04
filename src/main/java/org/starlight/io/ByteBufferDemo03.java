package org.starlight.io;

import static org.starlight.util.ByteBufferPrinter.debugAll;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.starlight.util.ByteBufferUtil;

@Slf4j
public class ByteBufferDemo03 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(46);
        buffer.put("Hello,world\n Iam zhangsan\nho".getBytes(StandardCharsets.UTF_8));
        ByteBufferUtil.split(buffer);
        buffer.put("w are you!\n".getBytes(StandardCharsets.UTF_8));
        ByteBufferUtil.split(buffer);
    }
}
