package com.devone.ptgvis;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigLoader {

    public static GeneratorParams load(String filePath) {
        try (InputStream in = Files.newInputStream(Paths.get(filePath))) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(in, GeneratorParams.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage(), e);
        }
    }
}
