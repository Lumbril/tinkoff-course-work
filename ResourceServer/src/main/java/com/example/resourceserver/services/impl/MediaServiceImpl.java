package com.example.resourceserver.services.impl;

import com.example.resourceserver.exceptions.IncorrectFileFormat;
import com.example.resourceserver.services.MediaService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {
    private final String url = new File("../persistentdata/media").getAbsolutePath();
    private final Path rootLocation = Paths.get(url);
    private final List<String> imageFormats = List.of(
            "image/png",
            "image/jpeg"
    );

    @Override
    public String saveImage(MultipartFile file) {
        if (!imageFormats.contains(file.getContentType())) {
            throw new IncorrectFileFormat("Фотография должна быть формата png, jpg");
        }

        UUID uuid = UUID.randomUUID();
        String filename = uuid + "-" + file.getOriginalFilename();

        Path destination = rootLocation.resolve(
                Paths.get(filename)
        ).normalize().toAbsolutePath();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "/media/" + filename;
    }
}
