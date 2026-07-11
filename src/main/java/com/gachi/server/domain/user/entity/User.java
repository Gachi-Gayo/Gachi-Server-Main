package com.gachi.server.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String providerId;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    private String mbtiType;

    private String healthConditions;

    private String allergies;

    @Enumerated(EnumType.STRING)
    private MobilityLevel mobilityLevel;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(unique = true)
    private String inviteCode;

    public void assignChildRole(String inviteCode) {
        this.role = UserRole.CHILD;
        this.inviteCode = inviteCode;
    }

    public void assignParentRole() {
        this.role = UserRole.PARENT;
    }
}
