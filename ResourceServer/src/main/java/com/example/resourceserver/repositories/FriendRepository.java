package com.example.resourceserver.repositories;

import com.example.resourceserver.entities.Friend;
import com.example.resourceserver.entities.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUser1_IdAndStatus(Long userId, FriendStatus status);
    List<Friend> findAllByUser2_IdAndStatus(Long userId, FriendStatus status);
    Optional<Friend> findByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);
    Optional<Friend> findByIdAndUser2_Id(Long id, Long user2Id);
    Optional<Friend> findByUser1_IdAndUser2_IdAndStatus(Long user1Id, Long user2Id, FriendStatus status);
    boolean existsByIdAndUser1_IdAndStatus(Long id, Long userId, FriendStatus status);
}
