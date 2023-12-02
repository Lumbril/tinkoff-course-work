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
    void removeFromFriend(Long user1Id, Long user2Id);
    void removeFromSubscribers(Long user1Id, Long user2Id);
    List<Friend> getRequests(Long userId);
    void removeRequest(Long id, Long userId);
    List<Friend> getSubscriptions(Long userId);
    void unsubscribe(Long user1Id, Long user2Id);
}
