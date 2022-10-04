package org.starlight;

import static org.starlight.util.ByteBufferPrinter.debugAll;

import java.nio.ByteBuffer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufferDemo01 {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(new byte[]{'a', 'b', 'c'});
        debugAll(buffer);
        log.info("{}", buffer.get(10));

        buffer.get(new byte[3]);
        debugAll(buffer);
        buffer.rewind();
        buffer.get();
        debugAll(buffer);

        //  mark 和reset搭配使用，是对rewind的增强
        log.info("{}", buffer.get());
        log.info("{}", buffer.get());
        buffer.mark();

        log.info("{}", buffer.get());
        log.info("{}", buffer.get());
        buffer.reset();
        log.info("{}", buffer.get());
        debugAll(buffer);
    }
}
