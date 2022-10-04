package org.starlight;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilesWalkFileTreeDemo00 {
    public static void main(String[] args) throws IOException {
        String rootPath = "./";
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get(rootPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.info("Dir==>{}", dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.info("File==>{}", file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        log.info("finished: dirs:{},files:{}", dirCount, fileCount);
    }
}
