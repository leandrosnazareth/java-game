package com.leandrosnazareth.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static String loadResource(String fileName) throws IOException {
        try (InputStream in = FileUtils.class.getResourceAsStream(fileName)) {
            if (in == null) {
                throw new IOException("Resource not found: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                return content.toString();
            }
        }
    }
}