package com.example.resourceserver;

import com.example.resourceserver.dto.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    private final String user1Token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6InN0cmluZyIsInRva2VuX3R5cGUiOiJhY2Nlc3MiLCJqdGkiOiIzNTkzNDY2Zi04YmY3LTRkODUtODc3Yy05YTUzYWU4YWUwMmYiLCJpYXQiOjE2OTk3ODY4NzEsImV4cCI6MTcwMjM3ODg3MX0.phG7T11TrevEdC1HUIArF0GsrhDXEzOg_-CexmdhVT6WCUS_gQi0YXBBUV7JhFlKKR56zyGOPNwVnCQKfmic9wGSrOyU7OA7ma-QcJl5CV5EGQvKsnGPRAguyNUwjM2sEKjH_Fj-CwfeG-wc03C4yfWgYJtc95JsTVdRY8FUQhMK8I4EkkGLFjrK7vaiozzOqyYQIFSkxPRs-evI6uMqR6FtvNGuH81OFnOBHxhKR53t_oqLlTPsBeTPvogt-xp1fGxr8anmL3iac0u5Dj1W03xpss1ud1JYAFDnNL5EUVgt9J6DpOEJjMiAj1RLCzItOLs5Q6IJA0VHqgnsstG_mA";

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() {

    }

    @Test
    public void notAuthUser() throws Exception {
        mockMvc.perform(get("/user/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void authUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(get("/user/me").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserResponse userResponseFromRequest = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertEquals(null, userResponseFromRequest.getFirstName());
        assertEquals(null, userResponseFromRequest.getLastName());
        assertEquals("string", userResponseFromRequest.getUsername());
    }
}
