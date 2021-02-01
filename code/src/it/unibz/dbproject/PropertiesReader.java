package it.unibz.dbproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesReader {
    private final String filePath;
    private final Properties properties;
    private Map<String, String> content;

    public PropertiesReader(String filePath) {
        if (!new File(filePath).exists())
            throw new IllegalArgumentException("File does not exist");

        this.properties = new Properties();
        this.filePath = filePath;

        read();
    }

    private void read() {
        try {
            properties.load(new FileInputStream(filePath));
            content = properties.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            e -> String.valueOf(e.getKey().toString()),
                            e -> String.valueOf(e.getValue())));
        } catch (IOException e) {
            content = new HashMap<>();
            throw new UncheckedIOException(e);
        }
    }

    public Map<String, String> getContent() {
        return content;
    }
}
