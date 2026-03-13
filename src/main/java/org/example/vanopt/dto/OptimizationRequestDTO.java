package org.example.vanopt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationRequestDTO {
    private int maxVolume;
    private List<ShipmentDTO> availableShipments;
}
