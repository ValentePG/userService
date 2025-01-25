package dev.valente.user_service.common;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class FileUtil {

    @Autowired
    private final ResourceLoader resourceLoader;

    @SneakyThrows
    public String readFile(String path) {
        var file = resourceLoader.getResource("classpath:" + path).getFile();

        return Files.readString(file.toPath());
    }

}
