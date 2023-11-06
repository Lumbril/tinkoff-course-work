package com.example.authserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "blacklist_refresh_token")
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "jti", nullable = false, unique = true)
    private String jti;
}
