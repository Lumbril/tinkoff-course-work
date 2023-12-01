package com.example.resourceserver.services.impl;

import com.example.resourceserver.entities.Friend;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.FriendStatus;
import com.example.resourceserver.exceptions.FriendException;
import com.example.resourceserver.repositories.FriendRepository;
import com.example.resourceserver.services.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;
    private final UserServiceImpl userService;

    @Override
    @Transactional(readOnly = true)
    public List<Friend> getFriends(Long userId) {
        return friendRepository.findAllByUser1_IdAndStatus(userId, FriendStatus.ACCEPTED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friend> getIncomingRequests(Long userId) {
        return friendRepository.findAllByUser2_IdAndStatus(userId, FriendStatus.WAITING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friend> getSubscribers(Long userId) {
        return friendRepository.findAllByUser2_IdAndStatus(userId, FriendStatus.DENIED);
    }

    @Override
    @Transactional
    public Friend createFriendRequest(Long whoId, Long whomId) {
        if (whoId == whomId) {
            throw new FriendException("Нельзя добавить в друзья самого себя");
        }

        if (checkIsExistsByUser1AndUser2(whoId, whomId)) {
            throw new FriendException("Вы уже отправили запрос на дружбу или уже являетесь ими");
        }

        User user1 = userService.getById(whoId);
        User user2 = userService.getById(whomId);

        Friend friend = Friend.builder()
                .user1(user1)
                .user2(user2)
                .status(FriendStatus.WAITING)
                .build();

        return friendRepository.save(friend);
    }

    @Override
    @Transactional
    public Friend moveToSubscribers(Long friendId, Long userId) {
        Optional<Friend> friendOptional = friendRepository.findByIdAndUser2_Id(friendId, userId);

        if (!friendOptional.isPresent()) {
            throw new FriendException("У вас нет запроса с таким id");
        }

        Friend friend = friendOptional.get();
        friend.setStatus(FriendStatus.DENIED);

        return friendRepository.save(friend);
    }

    @Override
    @Transactional
    public void moveToFriend(Long friendId, Long userId) {
        Optional<Friend> friendOptional = friendRepository.findByIdAndUser2_Id(friendId, userId);

        if (!friendOptional.isPresent()) {
            throw new FriendException("У вас нет запроса с таким id");
        }

        Friend friend = friendOptional.get();

        if (!(friend.getStatus() == FriendStatus.DENIED || friend.getStatus() == FriendStatus.WAITING)) {
            throw new FriendException("В друзья можно добавить только подписчиков или пользователя, отправившего запрос");
        }

        friend.setStatus(FriendStatus.ACCEPTED);

        Friend secondFriend = Friend.builder()
                .user1(friend.getUser2())
                .user2(friend.getUser1())
                .status(FriendStatus.ACCEPTED)
                .build();

        friendRepository.save(friend);
        friendRepository.save(secondFriend);
    }

    private boolean checkIsExistsByUser1AndUser2(Long whoId, Long whomId) {
        return friendRepository.findByUser1_IdAndUser2_Id(whoId, whomId).isPresent();
    }
}
