package org.starlight.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.starlight.util.ByteBufferPrinter;

@Slf4j
public class ByteBufDemo02 {
    public static void main(String[] args) {
//        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log.info("{}", buf);

        ByteBuf buf1 = buf.slice(0, 5);
        ByteBuf buf2 = buf.slice(5, 5);

        ByteBufferPrinter.log(buf1);
        ByteBufferPrinter.log(buf2);

        buf1.setByte(0, 'x');
        ByteBufferPrinter.log(buf1);
        ByteBufferPrinter.log(buf);
    }
}
