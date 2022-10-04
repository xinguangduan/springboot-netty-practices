package org.starlight;

import static org.starlight.util.ByteBufferPrinter.debugAll;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufferDemo02 {
    public static void main(String[] args) {
        // 字符串转byteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello world".getBytes(StandardCharsets.UTF_8));
        debugAll(buffer);
        // Charset

        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello world");
        debugAll(buffer1);
        // Wrapper
        ByteBuffer buffer2 = ByteBuffer.wrap("hello world".getBytes());
        debugAll(buffer2);

        buffer1.flip();
        String s = StandardCharsets.UTF_8.decode(buffer1).toString();
        log.info(s);
    }
}
