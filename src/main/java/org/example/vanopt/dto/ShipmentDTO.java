package org.example.vanopt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDTO {
    private String name;
    private int volume;
    private int revenue;
}
