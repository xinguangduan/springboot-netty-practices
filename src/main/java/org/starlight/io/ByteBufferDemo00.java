package org.starlight.io;

import static org.starlight.util.ByteBufferPrinter.debugAll;

import java.nio.ByteBuffer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufferDemo00 {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{0x61, 0x62});
        buffer.put((byte) 0x66);
        buffer.put((byte) 0x69);
        debugAll(buffer);
        buffer.flip();
        log.info("{}",buffer.get());
        buffer.compact();
        debugAll(buffer);

        System.out.println("ByteBuffer.allocate(10) "  +ByteBuffer.allocate(10).getClass());
        System.out.println("ByteBuffer.allocateDirect(10) "  +ByteBuffer.allocateDirect(10).getClass());

    }
}
