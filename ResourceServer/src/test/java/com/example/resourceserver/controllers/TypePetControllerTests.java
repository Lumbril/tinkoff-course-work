package com.example.resourceserver.controllers;

import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.dto.response.TypePetResponse;
import com.example.resourceserver.entities.TypePet;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.services.impl.TypePetServiceImpl;
import com.example.resourceserver.services.impl.UserServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class TypePetControllerTests {
    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private TypePetServiceImpl typePetService;

    @Autowired
    private MockMvc mockMvc;

    private final String user1Token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6InN0cmluZyIsInRva2VuX3R5cGUiOiJhY2Nlc3MiLCJqdGkiOiIzNTkzNDY2Zi04YmY3LTRkODUtODc3Yy05YTUzYWU4YWUwMmYiLCJpYXQiOjE2OTk3ODY4NzEsImV4cCI6MTcwMjM3ODg3MX0.phG7T11TrevEdC1HUIArF0GsrhDXEzOg_-CexmdhVT6WCUS_gQi0YXBBUV7JhFlKKR56zyGOPNwVnCQKfmic9wGSrOyU7OA7ma-QcJl5CV5EGQvKsnGPRAguyNUwjM2sEKjH_Fj-CwfeG-wc03C4yfWgYJtc95JsTVdRY8FUQhMK8I4EkkGLFjrK7vaiozzOqyYQIFSkxPRs-evI6uMqR6FtvNGuH81OFnOBHxhKR53t_oqLlTPsBeTPvogt-xp1fGxr8anmL3iac0u5Dj1W03xpss1ud1JYAFDnNL5EUVgt9J6DpOEJjMiAj1RLCzItOLs5Q6IJA0VHqgnsstG_mA";
    private final User tempUser = new User(1L, 1L,
            "string", null,
            null, Role.ROLE_USER);
    private final TypePet tempTypePet = new TypePet(1L, "собака");
    private final TypePetResponse tempTypePetResponse = new TypePetResponse(tempTypePet.getId(), tempTypePet.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void doGetAccessTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(tempTypePet).when(typePetService).get(anyLong());

        MvcResult result = mockMvc.perform(get("/type_pet/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        TypePetResponse typePetResponse = objectMapper.readValue(response.getContentAsString(), TypePetResponse.class);

        assertEquals(tempTypePetResponse, typePetResponse);
    }

    @Test
    public void doGetNotExistsTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(null).when(typePetService).get(anyLong());

        MvcResult result = mockMvc.perform(get("/type_pet/0").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Тип домашнего животного с таким id не найден", errorResponse.getError());
    }

    @Test
    public void getListTest() throws Exception {
        Mockito.doReturn(tempUser).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(List.of(tempTypePet)).when(typePetService).getAll();

        MvcResult result = mockMvc.perform(get("/type_pet/list").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        List<TypePetResponse> typePetResponseList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<TypePetResponse>>(){});

        assertIterableEquals(List.of(tempTypePetResponse), typePetResponseList);
    }
}
