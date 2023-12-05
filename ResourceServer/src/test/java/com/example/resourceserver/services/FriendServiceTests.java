package com.example.resourceserver.services;

import com.example.resourceserver.entities.Friend;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.FriendStatus;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.exceptions.FriendException;
import com.example.resourceserver.exceptions.IncorrectFileFormat;
import com.example.resourceserver.repositories.FriendRepository;
import com.example.resourceserver.services.impl.FriendServiceImpl;
import com.example.resourceserver.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTests {
    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FriendServiceImpl friendService;

    private final User tempUser1 = new User(1L, 1L,
            "user1", null,
            null, Role.ROLE_USER);
    private final User tempUser2 = new User(2L, 2L,
            "user2", null,
            null, Role.ROLE_USER);
    private final Friend tempFriendUser1 = new Friend(1L, tempUser1, tempUser2, FriendStatus.ACCEPTED);
    private final Friend tempFriendRequest = new Friend(1L, tempUser1, tempUser2, FriendStatus.WAITING);
    private final Friend tempSubscriber = new Friend(1L, tempUser1, tempUser2, FriendStatus.DENIED);

    @Test
    public void getFriendsTest() {
        Mockito.doReturn(List.of(tempFriendUser1)).when(friendRepository).findAllByUser1_IdAndStatus(anyLong(), any());

        List<Friend> friends = friendService.getFriends(1L);

        assertIterableEquals(List.of(tempFriendUser1), friends);
    }

    @Test
    public void getIncomingRequestsTest() {
        Mockito.doReturn(List.of(tempFriendRequest)).when(friendRepository).findAllByUser2_IdAndStatus(anyLong(), any());

        List<Friend> incomingRequest = friendService.getIncomingRequests(2L);

        assertIterableEquals(List.of(tempFriendRequest), incomingRequest);
    }

    @Test
    public void getSubscribersTest() {
        Mockito.doReturn(List.of(tempSubscriber)).when(friendRepository).findAllByUser2_IdAndStatus(anyLong(), any());

        List<Friend> subscribers = friendService.getIncomingRequests(2L);

        assertIterableEquals(List.of(tempSubscriber), subscribers);
    }

    @Test
    public void createFriendRequestFromMyselfTest() {
        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.createFriendRequest(1L, 1L));

        assertEquals("Нельзя добавить в друзья самого себя", ex.getMessage());
    }

    @Test
    public void createFriendRequestWhenExistsTest() {
        Mockito.doReturn(Optional.of(tempFriendUser1)).when(friendRepository).findByUser1_IdAndUser2_Id(anyLong(), any());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.createFriendRequest(1L, 2L));

        assertEquals("Вы уже отправили запрос на дружбу или уже являетесь ими", ex.getMessage());
    }

    @Test
    public void createFriendFromRequestAccessTest() {
        Mockito.doReturn(Optional.empty()).when(friendRepository).findByUser1_IdAndUser2_Id(anyLong(), any());
        Mockito.doReturn(tempUser1).when(userService).getById(1L);
        Mockito.doReturn(tempUser2).when(userService).getById(2L);
        Mockito.doReturn(new Friend(1L, tempUser1, tempUser2, FriendStatus.WAITING)).when(friendRepository).save(any());

        Friend friend = friendService.createFriendRequest(1L, 2L);

        assertEquals(tempFriendRequest, friend);
    }

    @Test
    public void moveToSubscribersWithoutRequestTest() {
        Mockito.doReturn(Optional.empty()).when(friendRepository).findByIdAndUser2_Id(anyLong(), any());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.moveToSubscribers(1L, 2L));

        assertEquals("У вас нет запроса с таким id", ex.getMessage());
    }

    @Test
    public void moveToSubscribersAccessTest() {
        Mockito.doReturn(Optional.of(tempFriendUser1)).when(friendRepository).findByIdAndUser2_Id(anyLong(), any());
        Mockito.doReturn(tempSubscriber).when(friendRepository).save(any());

        Friend friend = friendService.moveToSubscribers(1L, 2L);

        assertEquals(tempSubscriber, friend);
    }

    @Test
    public void moveToFriendWithoutRequestTest() {
        Mockito.doReturn(Optional.empty()).when(friendRepository).findByIdAndUser2_Id(anyLong(), anyLong());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.moveToFriend(1L, 2L));

        assertEquals("У вас нет запроса с таким id", ex.getMessage());
    }

    @Test
    public void moveToFriendWhenFriendTest() {
        Mockito.doReturn(Optional.of(tempFriendUser1)).when(friendRepository).findByIdAndUser2_Id(anyLong(), anyLong());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.moveToFriend(1L, 2L));

        assertEquals("В друзья можно добавить только подписчиков или пользователя, отправившего запрос", ex.getMessage());
    }

    @Test
    public void moveToFriendAccessTest() {
        Mockito.doReturn(Optional.of(tempSubscriber)).when(friendRepository).findByIdAndUser2_Id(anyLong(), anyLong());

        friendService.moveToFriend(1L, 2L);

        verify(friendRepository, times(2)).save(any());
    }

    @Test
    public void removeFromFriendWithoutFriendTest() {
        Mockito.doReturn(Optional.empty()).when(friendRepository).findByUser1_IdAndUser2_IdAndStatus(anyLong(), anyLong(), any());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.removeFromFriend(1L, 2L));

        assertEquals("Вы не являетесь друзьями", ex.getMessage());
    }

    @Test
    public void removeFromFriendTest() {
        Mockito.doReturn(Optional.of(tempFriendUser1)).when(friendRepository).findByUser1_IdAndUser2_IdAndStatus(anyLong(), anyLong(), any());

        friendService.removeFromFriend(1L, 2L);

        verify(friendRepository, times(1)).delete(any());
        verify(friendRepository, times(1)).save(any());
    }

    @Test
    public void removeFromSubscribersWithoutSubscriberTest() {
        Mockito.doReturn(Optional.empty()).when(friendRepository).findByUser1_IdAndUser2_IdAndStatus(anyLong(), anyLong(), any());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.removeFromSubscribers(1L, 2L));

        assertEquals("У вас нет такого подписчика", ex.getMessage());
    }

    @Test
    public void removeFromSubscribersAccessTest() {
        Mockito.doReturn(Optional.of(tempSubscriber)).when(friendRepository).findByUser1_IdAndUser2_IdAndStatus(anyLong(), anyLong(), any());

        friendService.removeFromSubscribers(1L, 2L);

        verify(friendRepository, times(1)).delete(any());
    }

    @Test
    public void getRequestsTest() {
        Mockito.doReturn(List.of(tempFriendRequest)).when(friendRepository).findAllByUser1_IdAndStatus(anyLong(), any());

        List<Friend> requests = friendService.getRequests(1L);

        assertIterableEquals(List.of(tempFriendRequest), requests);
    }

    @Test
    public void removeRequestWithoutRequestTest() {
        Mockito.doReturn(false).when(friendRepository).existsByIdAndUser1_IdAndStatus(anyLong(), anyLong(), any());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.removeRequest(1L, 1L));

        assertEquals("У вас нет такого запроса на дружбу", ex.getMessage());
    }

    @Test
    public void removeRequestAccessTest() {
        Mockito.doReturn(true).when(friendRepository).existsByIdAndUser1_IdAndStatus(anyLong(), anyLong(), any());

        friendService.removeRequest(1L, 1L);

        verify(friendRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void getSubscriptionsTest() {
        Mockito.doReturn(List.of(tempSubscriber)).when(friendRepository).findAllByUser1_IdAndStatus(anyLong(), any());

        List<Friend> subscriptions = friendService.getSubscriptions(2L);

        assertIterableEquals(List.of(tempSubscriber), subscriptions);
    }

    @Test
    public void unsubscribeWithoutSubscriberTest() {
        Mockito.doReturn(Optional.empty()).when(friendRepository).findByUser1_IdAndUser2_IdAndStatus(anyLong(), anyLong(), any());

        FriendException ex = assertThrows(FriendException.class,
                () -> friendService.unsubscribe(1L, 1L));

        assertEquals("У вас нет подписки на этого пользователя", ex.getMessage());
    }

    @Test
    public void unsubscribeAccessTest() {
        Mockito.doReturn(Optional.of(tempSubscriber)).when(friendRepository).findByUser1_IdAndUser2_IdAndStatus(anyLong(), anyLong(), any());

        friendService.unsubscribe(1L, 2L);

        verify(friendRepository, times(1)).delete(any());
    }
}
