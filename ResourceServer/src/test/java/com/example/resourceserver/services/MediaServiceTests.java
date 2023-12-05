package com.example.resourceserver.services;

import com.example.resourceserver.exceptions.IncorrectFileFormat;
import com.example.resourceserver.services.impl.MediaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTests {
    private final MultipartFile tempCorrectImg = new MockMultipartFile("img.png", "img.png", "image/png",
            new byte[]{0, 0, 0, 0, 0, 0});
    private final MultipartFile tempIncorrectImg = new MockMultipartFile("img.txt", "img.text", "text/plain",
                                                                              new byte[]{0, 0, 0, 0, 0, 0});

    @InjectMocks
    private MediaServiceImpl mediaService;

    @Test
    public void saveImageCorrectFileTest() {
        Mockito.mockStatic(Files.class).when(() -> Files.copy((Path) any(), any(), any())).thenReturn(null);

        String imgUrl = mediaService.saveImage(tempCorrectImg);

        assertNotEquals(null, imgUrl);
    }

    @Test
    public void saveImageIncorrectFileTest() {
        IncorrectFileFormat ex = assertThrows(IncorrectFileFormat.class, () -> {
            mediaService.saveImage(tempIncorrectImg);
        });

        assertEquals("Фотография должна быть формата png, jpg", ex.getMessage());
    }
}
