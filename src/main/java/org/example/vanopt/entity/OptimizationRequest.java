package org.example.vanopt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import java.util.List;


@Entity
@Table(name = "optimization_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizationRequest {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "max_volume", nullable = false)
    private int maxVolume;

    @Column(name = "total_volume", nullable = false)
    private int totalVolume;

    @Column(name = "total_revenue", nullable = false)
    private int totalRevenue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shipment> shipments;

    public static OptimizationRequest createNew(int maxVolume) {
        OptimizationRequest request = new OptimizationRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMaxVolume(maxVolume);
        request.setCreatedAt(Instant.now());
        return request;
    }
}
