package org.starlight.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.starlight.util.ByteBufferPrinter;

@Slf4j
public class ByteBufDemo03 {
    public static void main(String[] args) {
//        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e'});
        buf2.writeBytes(new byte[]{'f', 'g', 'h', 'i', 'j'});
        log.info("{}", buf1);
        log.info("{}", buf2);


        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeByteBuf.addComponents(true, buf1, buf2);

        ByteBufferPrinter.log(compositeByteBuf);
    }
}
