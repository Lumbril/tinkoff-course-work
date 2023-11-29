package com.example.resourceserver.entities;

import com.example.resourceserver.entities.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "friend")
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id")
    private User user2;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendStatus status;
}
