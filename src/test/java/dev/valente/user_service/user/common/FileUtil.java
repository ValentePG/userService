package dev.valente.user_service.user.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class FileUtil {

    @Autowired
    private final ResourceLoader resourceLoader;

    public String readFile(String path) throws IOException {
        var file = resourceLoader.getResource("classpath:" + path).getFile();

        return Files.readString(file.toPath());
    }
}
