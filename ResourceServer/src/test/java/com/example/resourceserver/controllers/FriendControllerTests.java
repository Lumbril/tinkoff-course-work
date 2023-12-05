package com.example.resourceserver.controllers;

import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.dto.response.FriendResponse;
import com.example.resourceserver.dto.response.UserResponse;
import com.example.resourceserver.entities.Friend;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.FriendStatus;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.exceptions.FriendException;
import com.example.resourceserver.services.impl.FriendServiceImpl;
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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FriendControllerTests {
    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private FriendServiceImpl friendService;

    @Autowired
    private MockMvc mockMvc;

    private final String user1Token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6InN0cmluZyIsInRva2VuX3R5cGUiOiJhY2Nlc3MiLCJqdGkiOiIzNTkzNDY2Zi04YmY3LTRkODUtODc3Yy05YTUzYWU4YWUwMmYiLCJpYXQiOjE2OTk3ODY4NzEsImV4cCI6MTcwMjM3ODg3MX0.phG7T11TrevEdC1HUIArF0GsrhDXEzOg_-CexmdhVT6WCUS_gQi0YXBBUV7JhFlKKR56zyGOPNwVnCQKfmic9wGSrOyU7OA7ma-QcJl5CV5EGQvKsnGPRAguyNUwjM2sEKjH_Fj-CwfeG-wc03C4yfWgYJtc95JsTVdRY8FUQhMK8I4EkkGLFjrK7vaiozzOqyYQIFSkxPRs-evI6uMqR6FtvNGuH81OFnOBHxhKR53t_oqLlTPsBeTPvogt-xp1fGxr8anmL3iac0u5Dj1W03xpss1ud1JYAFDnNL5EUVgt9J6DpOEJjMiAj1RLCzItOLs5Q6IJA0VHqgnsstG_mA";
    private final User user1 = new User(1L, 1L,
            "user1", null,
            null, Role.ROLE_USER);
    private final User user2 = new User(2L, 2L,
            "user2", null,
            null, Role.ROLE_USER);
    private final Friend friendUser1 = new Friend(1L, user1, user2, FriendStatus.ACCEPTED);
    private final Friend friendRequest = new Friend(1L, user2, user1, FriendStatus.WAITING);
    private final Friend myFriendRequest = new Friend(1L, user1, user2, FriendStatus.WAITING);
    private final Friend mySubscription = new Friend(1L, user1, user2, FriendStatus.DENIED);
    private final Friend subscriber = new Friend(1L, user2, user1, FriendStatus.DENIED);
    private final UserResponse user1Response = new UserResponse(user1.getId(), user1.getUsername(),
            user1.getFirstName(), user1.getLastName());
    private final UserResponse user2Response = new UserResponse(user2.getId(), user2.getUsername(),
            user2.getFirstName(), user2.getLastName());
    private final FriendResponse friendResponseForFriend = new FriendResponse(1L, user2Response, FriendStatus.ACCEPTED);
    private final FriendResponse friendResponseForIncomingRequest = new FriendResponse(1L, user2Response, FriendStatus.WAITING);
    private final FriendResponse friendResponseForSubscriber = new FriendResponse(1L, user2Response, FriendStatus.DENIED);
    private final FriendResponse friendResponseForMyRequest = new FriendResponse(1L, user2Response, FriendStatus.WAITING);
    private final FriendResponse friendResponseForMySubscription = new FriendResponse(1L, user2Response, FriendStatus.DENIED);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getFriendsTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(List.of(friendUser1)).when(friendService).getFriends(anyLong());

        MvcResult result = mockMvc.perform(get("/friend/list").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        List<FriendResponse> friendResponseList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FriendResponse>>(){});

        assertIterableEquals(List.of(friendResponseForFriend), friendResponseList);
    }

    @Test
    public void getIncomingRequests() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(List.of(friendRequest)).when(friendService).getIncomingRequests(anyLong());

        MvcResult result = mockMvc.perform(get("/friend/request/list").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        List<FriendResponse> friendResponseList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FriendResponse>>(){});

        assertIterableEquals(List.of(friendResponseForIncomingRequest), friendResponseList);
    }

    @Test
    public void getSubscribersTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(List.of(subscriber)).when(friendService).getSubscribers(anyLong());

        MvcResult result = mockMvc.perform(get("/friend/subscriber/list").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        List<FriendResponse> friendResponseList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FriendResponse>>(){});

        assertIterableEquals(List.of(friendResponseForSubscriber), friendResponseList);
    }

    @Test
    public void createFriendRequestWithMyselfTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("Нельзя добавить в друзья самого себя")).when(friendService).createFriendRequest(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(post("/friend/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Нельзя добавить в друзья самого себя", errorResponse.getError());
    }

    @Test
    public void createFriendRequestWithExistsRequestTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("Вы уже отправили запрос на дружбу или уже являетесь ими")).when(friendService).createFriendRequest(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(post("/friend/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Вы уже отправили запрос на дружбу или уже являетесь ими", errorResponse.getError());
    }

    @Test
    public void createFriendRequestNotFoundSecondUserTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new NoSuchElementException("No value present")).when(friendService).createFriendRequest(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(post("/friend/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Пользователь с таким id не найден", errorResponse.getError());
    }

    @Test
    public void createFriendRequestAccessTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(friendUser1).when(friendService).createFriendRequest(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(post("/friend/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        FriendResponse friendResponse = objectMapper.readValue(response.getContentAsString(), FriendResponse.class);

        assertEquals(friendResponseForFriend, friendResponse);
    }

    @Test
    public void acceptRequestWithoutRequestTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("У вас нет запроса с таким id")).when(friendService).moveToFriend(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(post("/friend/request/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("У вас нет запроса с таким id", errorResponse.getError());
    }

    @Test
    public void acceptRequestWhenFriendTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("В друзья можно добавить только подписчиков или пользователя, отправившего запрос")).when(friendService).moveToFriend(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(post("/friend/request/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("В друзья можно добавить только подписчиков или пользователя, отправившего запрос", errorResponse.getError());
    }

    @Test
    public void acceptRequestAccessTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doNothing().when(friendService).moveToFriend(anyLong(), anyLong());

        mockMvc.perform(post("/friend/request/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(friendService, times(1)).moveToFriend(anyLong(), anyLong());
    }

    @Test
    public void deleteRequestWhenNotExistsTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("У вас нет запроса с таким id")).when(friendService).moveToSubscribers(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(delete("/friend/request/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("У вас нет запроса с таким id", errorResponse.getError());
    }

    @Test
    public void deleteRequestAccessTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(subscriber).when(friendService).moveToSubscribers(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(delete("/friend/request/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        FriendResponse friendResponse = objectMapper.readValue(response.getContentAsString(), FriendResponse.class);

        assertEquals(friendResponseForSubscriber, friendResponse);
    }

    @Test
    public void deleteFriendWhenNotFriendTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("Вы не являетесь друзьями")).when(friendService).removeFromFriend(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(delete("/friend/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Вы не являетесь друзьями", errorResponse.getError());
    }

    @Test
    public void deleteFriendAccessTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doNothing().when(friendService).removeFromFriend(anyLong(), anyLong());

        mockMvc.perform(delete("/friend/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteSubscriberWhenNotSubscriberTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("У вас нет такого подписчика")).when(friendService).removeFromSubscribers(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(delete("/friend/subscribe/my/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("У вас нет такого подписчика", errorResponse.getError());
    }

    @Test
    public void deleteSubscriberAccessTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doNothing().when(friendService).removeFromSubscribers(anyLong(), anyLong());

        mockMvc.perform(delete("/friend/subscribe/my/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getMyRequestsTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(List.of(myFriendRequest)).when(friendService).getRequests(anyLong());

        MvcResult result = mockMvc.perform(get("/friend/request/my/list").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        List<FriendResponse> friendResponseList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FriendResponse>>(){});

        assertIterableEquals(List.of(friendResponseForMyRequest), friendResponseList);
    }

    @Test
    public void deleteMyRequestWhenNotExistsTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("У вас нет такого запроса на дружбу")).when(friendService).removeRequest(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(delete("/friend/request/my/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("У вас нет такого запроса на дружбу", errorResponse.getError());
    }

    @Test
    public void deleteMyRequestAccessTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doNothing().when(friendService).removeRequest(anyLong(), anyLong());

        mockMvc.perform(delete("/friend/request/my/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getMySubscriptionsTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doReturn(List.of(mySubscription)).when(friendService).getSubscriptions(anyLong());

        MvcResult result = mockMvc.perform(get("/friend/subscriptions/list").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        List<FriendResponse> subscriptionList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FriendResponse>>(){});

        assertIterableEquals(List.of(friendResponseForMySubscription), subscriptionList);
    }

    @Test
    public void unsubscribeWhenNotExistsTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doThrow(new FriendException("У вас нет подписки на этого пользователя")).when(friendService).unsubscribe(anyLong(), anyLong());

        MvcResult result = mockMvc.perform(delete("/friend/subscription/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("У вас нет подписки на этого пользователя", errorResponse.getError());
    }

    @Test
    public void unsubscribeAccessTest() throws Exception {
        Mockito.doReturn(user1).when(userService).getByAuthIdOrCreateFromTokenBody(any());
        Mockito.doNothing().when(friendService).unsubscribe(anyLong(), anyLong());

        mockMvc.perform(delete("/friend/subscription/1").header("Authorization", "Bearer " + user1Token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
