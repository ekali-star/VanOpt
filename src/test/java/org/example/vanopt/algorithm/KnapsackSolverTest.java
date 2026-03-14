package org.example.vanopt.algorithm;

import org.example.vanopt.dto.ShipmentDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnapsackSolverTest {

    @Test
    void testSolve_normalCase() {
        List<ShipmentDTO> shipments = Arrays.asList(
                new ShipmentDTO("A", 5, 120),
                new ShipmentDTO("B", 10, 200),
                new ShipmentDTO("C", 3, 80),
                new ShipmentDTO("D", 8, 160)
        );

        List<ShipmentDTO> result = KnapsackSolver.solve(15, shipments);

        assertEquals(2, result.size());
        assertTrue(result.contains(shipments.get(0)));
        assertTrue(result.contains(shipments.get(1)));
    }

    @Test
    void testSolve_emptyShipments() {
        List<ShipmentDTO> result = KnapsackSolver.solve(10, Collections.emptyList());
        assertTrue(result.isEmpty(), "Result should be empty when no shipments are provided");
    }

    @Test
    void testSolve_zeroMaxVolume() {
        List<ShipmentDTO> shipments = Arrays.asList(
                new ShipmentDTO("A", 5, 10),
                new ShipmentDTO("B", 4, 40)
        );
        List<ShipmentDTO> result = KnapsackSolver.solve(0, shipments);
        assertTrue(result.isEmpty(), "Result should be empty when maxVolume is 0");
    }

    @Test
    void testSolve_allShipmentsFit() {
        List<ShipmentDTO> shipments = Arrays.asList(
                new ShipmentDTO("A", 1, 10),
                new ShipmentDTO("B", 2, 20),
                new ShipmentDTO("C", 3, 30)
        );

        List<ShipmentDTO> result = KnapsackSolver.solve(10, shipments);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(shipments));
    }

    @Test
    void testSolve_someShipmentsTooBig() {
        List<ShipmentDTO> shipments = Arrays.asList(
                new ShipmentDTO("A", 15, 100),
                new ShipmentDTO("B", 4, 40),
                new ShipmentDTO("C", 6, 30)
        );

        List<ShipmentDTO> result = KnapsackSolver.solve(10, shipments);

        assertEquals(2, result.size());
        assertTrue(result.contains(shipments.get(1)));
        assertTrue(result.contains(shipments.get(2)));
    }
}