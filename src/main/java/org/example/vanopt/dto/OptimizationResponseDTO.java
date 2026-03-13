package org.example.vanopt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizationResponseDTO {
    private String requestId;
    private List<ShipmentDTO> selectedShipments;
    private int totalVolume;
    private int totalRevenue;
    private Instant createdAt;
}
