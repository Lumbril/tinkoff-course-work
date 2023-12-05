package com.example.resourceserver.controllers;

import com.example.resourceserver.dto.request.PetRequest;
import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.dto.response.PetResponse;
import com.example.resourceserver.entities.Pet;
import com.example.resourceserver.entities.TypePet;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.services.impl.PetServiceImpl;
import com.example.resourceserver.services.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PetControllerTests {
    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private PetServiceImpl petService;

    @Autowired
    private MockMvc mockMvc;

    private final String user1Token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6InN0cmluZyIsInRva2VuX3R5cGUiOiJhY2Nlc3MiLCJqdGkiOiIzNTkzNDY2Zi04YmY3LTRkODUtODc3Yy05YTUzYWU4YWUwMmYiLCJpYXQiOjE2OTk3ODY4NzEsImV4cCI6MTcwMjM3ODg3MX0.phG7T11TrevEdC1HUIArF0GsrhDXEzOg_-CexmdhVT6WCUS_gQi0YXBBUV7JhFlKKR56zyGOPNwVnCQKfmic9wGSrOyU7OA7ma-QcJl5CV5EGQvKsnGPRAguyNUwjM2sEKjH_Fj-CwfeG-wc03C4yfWgYJtc95JsTVdRY8FUQhMK8I4EkkGLFjrK7vaiozzOqyYQIFSkxPRs-evI6uMqR6FtvNGuH81OFnOBHxhKR53t_oqLlTPsBeTPvogt-xp1fGxr8anmL3iac0u5Dj1W03xpss1ud1JYAFDnNL5EUVgt9J6DpOEJjMiAj1RLCzItOLs5Q6IJA0VHqgnsstG_mA";

    private final User tempUser = new User(1L, 1L,
            "string", null,
            null, Role.ROLE_USER);
    private final TypePet tempTypePet = new TypePet(1L, "собака");
    private final Pet tempPet = new Pet(1L, tempTypePet, "Жучка", false, null, tempUser);
    private final Pet tempPetWithImage = new Pet(1L, tempTypePet,
            "Жучка", false,
            "path/to/img.png", tempUser);
    private final PetResponse tempPetResponse = new PetResponse(1L, tempTypePet,
            tempPet.getName(), tempPet.getGender(),
            tempPet.getImage(), tempUser.getId());
    private final PetResponse tempPetResponseWithImage = new PetResponse(1L, tempTypePet,
            tempPetWithImage.getName(), tempPetWithImage.getGender(),
            tempPetWithImage.getImage(), tempUser.getId());
    private final PetRequest tempPetRequestValid = new PetRequest(1L, "Жучка", true);
    private final PetRequest tempPetRequestNotValid = new PetRequest(1L, null, true);
    private final MockMultipartFile tempCorrectImg = new MockMultipartFile("img.png", "img.png", "image/png",
            new byte[]{});
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void doGetAccessTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(tempPet).when(petService).get(anyLong());

        MvcResult result = mockMvc.perform(get("/pet/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        PetResponse petResponse = objectMapper.readValue(response.getContentAsString(), PetResponse.class);

        assertEquals(tempPetResponse, petResponse);
    }

    @Test
    public void doGetNotExistsTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(NoSuchElementException.class).when(petService).get(anyLong());

        MvcResult result = mockMvc.perform(get("/pet/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Питомец с таким id не найден", errorResponse.getError());
    }

    @Test
    public void createAccessTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(tempPet).when(petService).create(any(), anyLong());

        MvcResult result = mockMvc.perform(post("/pet")
                        .header("Authorization", "Bearer " + user1Token)
                        .content(objectMapper.writeValueAsString(tempPetRequestValid))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        PetResponse petResponse = objectMapper.readValue(response.getContentAsString(), PetResponse.class);

        assertEquals(tempPetResponse, petResponse);
    }

    @Test
    public void createFromNotValidTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());

        MvcResult result = mockMvc.perform(post("/pet")
                        .header("Authorization", "Bearer " + user1Token)
                        .content(objectMapper.writeValueAsString(tempPetRequestNotValid))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Ошибка валидации", errorResponse.getError());
    }

    @Test
    public void saveImageTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(tempPetWithImage).when(petService).updateImage(anyLong(), anyLong(), any());

        MvcResult result = mockMvc.perform(multipart("/pet/1")
                        .file(tempCorrectImg)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        PetResponse petResponse = objectMapper.readValue(response.getContentAsString(), PetResponse.class);

        assertEquals(tempPetResponseWithImage, petResponse);
    }

    @Test
    public void deleteTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doNothing().when(petService).deleteByIdAndUser(anyLong(), anyLong());

        mockMvc.perform(delete("/pet/1")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(petService, times(1)).deleteByIdAndUser(anyLong(), anyLong());
    }
}
