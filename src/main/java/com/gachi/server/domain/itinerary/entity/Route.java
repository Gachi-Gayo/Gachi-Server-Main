package com.gachi.server.domain.itinerary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_block_id", nullable = false)
    private Block startBlock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_block_id", nullable = false)
    private Block endBlock;

    @Enumerated(EnumType.STRING)
    private TransportType transportType;

    private Integer duration;

    private Integer distance;

    private Integer tollCost;

    private Integer taxiFare;

    private Integer fuelCost;
}
