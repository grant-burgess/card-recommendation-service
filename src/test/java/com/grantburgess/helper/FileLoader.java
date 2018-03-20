package com.grantburgess.helper;

import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;

public class FileLoader {
    private FileLoader() { }

    public static String read(String filePath) throws IOException {
        return new String(
                Files.readAllBytes(
                        ResourceUtils.getFile(filePath).toPath()
                )
        );
    }
}