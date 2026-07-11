package com.gachi.server.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_disease")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_profile_id", nullable = false)
    private ParentProfile parentProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiseaseCategory category;

    @Column(nullable = false)
    private String diseaseName;
}
