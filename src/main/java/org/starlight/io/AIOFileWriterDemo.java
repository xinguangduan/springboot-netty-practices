package org.starlight.io;

import static org.starlight.util.ByteBufferPrinter.debugAll;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AIOFileWriterDemo {
    static final String rootPath = "/";

    public static void main(String[] args) throws IOException {

        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(rootPath + "datawrite.txt"), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.info("read start...");
            channel.read(buffer, 0, buffer, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.info("read completed.");
                    attachment.flip();
                    debugAll(attachment);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.info("read end");
            System.in.read();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
