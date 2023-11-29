package com.example.resourceserver.services;

import com.example.resourceserver.entities.Friend;

import java.util.List;

public interface FriendService {
    List<Friend> getFriends(Long userId);
    List<Friend> getIncomingRequests(Long userId);
    List<Friend> getSubscribers(Long userId);
    Friend createFriendRequest(Long whoId, Long whomId);
    Friend moveToSubscribers(Long friendId, Long userId);
    void moveToFriend(Long friendId, Long userId);
}
