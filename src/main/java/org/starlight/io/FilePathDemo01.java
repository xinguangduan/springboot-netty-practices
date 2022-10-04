package org.starlight.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FilePathDemo01 {
    public static void main(String[] args) throws IOException {

        Path source = Paths.get("./../from.txt");
        Path target = Paths.get("./../to.txt");
        System.out.println(source.normalize());
        boolean e = Files.exists(source);
        System.out.println(e);

        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
