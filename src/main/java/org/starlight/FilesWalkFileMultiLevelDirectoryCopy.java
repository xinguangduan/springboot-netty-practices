package org.starlight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesWalkFileMultiLevelDirectoryCopy {
    public static void main(String[] args) throws IOException {
        String source = "";
        String target = "";
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                Path p = Paths.get(targetName);
                if (Files.isDirectory(path)) {
                    Files.createDirectory(p);
                } else if (Files.isRegularFile(path)) {
                    Files.copy(path, p);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
