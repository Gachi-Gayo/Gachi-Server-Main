package com.gachi.server.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_profile")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_user_id", nullable = false)
    private User child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id")
    private User parent;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MobilityLevel mobilityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileStatus status;

    public void linkParent(User parent) {
        this.parent = parent;
    }

    public void update(String name, MobilityLevel mobilityLevel, ProfileStatus status) {
        this.name = name;
        this.mobilityLevel = mobilityLevel;
        this.status = status;
    }
}
