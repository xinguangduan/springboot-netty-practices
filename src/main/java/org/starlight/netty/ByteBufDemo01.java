package org.starlight.netty;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.starlight.util.ByteBufferPrinter;

@Slf4j
public class ByteBufDemo01 {
    public static void main(String[] args) {
//        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        log.info("{}", buf);
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            str.append("hello guest");
        }
        buf.writeBytes(str.toString().getBytes(StandardCharsets.UTF_8));
        log.info("{}", buf);
        ByteBufferPrinter.log(buf);
        boolean res = ReferenceCountUtil.release(buf);
        log.info("release {}", res);
    }
}
