package com.gachi.server.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "places")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String contentId;

    private String contentTypeId;

    @Column(nullable = false)
    private String name;

    private Double latitude;

    private Double longitude;

    private String address;

    private String firstImageUrl;
}
