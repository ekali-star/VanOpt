package org.example.vanopt.service;

import org.example.vanopt.algorithm.KnapsackSolver;
import org.example.vanopt.dto.OptimizationRequestDTO;
import org.example.vanopt.dto.OptimizationResponseDTO;
import org.example.vanopt.dto.ShipmentDTO;
import org.example.vanopt.entity.OptimizationRequest;
import org.example.vanopt.entity.Shipment;
import org.example.vanopt.repository.OptimizationRequestRepository;
import org.example.vanopt.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OptimizationServiceTest {

    private OptimizationRequestRepository optimizationRequestRepository;
    private ShipmentRepository shipmentRepository;
    private OptimizationService optimizationService;

    @BeforeEach
    void setUp() {
        optimizationRequestRepository = mock(OptimizationRequestRepository.class);
        shipmentRepository = mock(ShipmentRepository.class);
        optimizationService = new OptimizationService(optimizationRequestRepository, shipmentRepository);
    }

    @Test
    void testOptimize_basicScenario() {
        List<ShipmentDTO> shipments = Arrays.asList(
                new ShipmentDTO("A", 5, 10),
                new ShipmentDTO("B", 4, 40),
                new ShipmentDTO("C", 6, 30),
                new ShipmentDTO("D", 3, 50)
        );

        OptimizationRequestDTO requestDTO = new OptimizationRequestDTO();
        requestDTO.setMaxVolume(10);
        requestDTO.setAvailableShipments(shipments);

        OptimizationResponseDTO response = optimizationService.optimize(requestDTO);

        assertNotNull(response.getRequestId());
        assertNotNull(response.getCreatedAt());
        assertEquals(2, response.getSelectedShipments().size());

        List<String> selectedNames = response.getSelectedShipments().stream()
                .map(ShipmentDTO::getName)
                .toList();

        assertTrue(selectedNames.contains("B"));
        assertTrue(selectedNames.contains("D"));

        ArgumentCaptor<OptimizationRequest> captor = ArgumentCaptor.forClass(OptimizationRequest.class);
        verify(optimizationRequestRepository, times(1)).save(captor.capture());

        OptimizationRequest savedRequest = captor.getValue();
        assertEquals(response.getTotalRevenue(), savedRequest.getTotalRevenue());
        assertEquals(response.getTotalVolume(), savedRequest.getTotalVolume());
        assertEquals(4, savedRequest.getShipments().size());
    }

    @Test
    void testBuildResponseFromEntity() {
        OptimizationRequest request = OptimizationRequest.createNew(10);
        Shipment selected = Shipment.builder()
                .name("B")
                .volume(4)
                .revenue(40)
                .selected(true)
                .request(request)
                .build();
        Shipment notSelected = Shipment.builder()
                .name("A")
                .volume(5)
                .revenue(10)
                .selected(false)
                .request(request)
                .build();

        request.setShipments(Arrays.asList(selected, notSelected));
        request.setTotalRevenue(40);
        request.setTotalVolume(4);

        OptimizationResponseDTO response = optimizationService.buildResponseFromEntity(request);

        assertEquals(request.getId(), response.getRequestId());
        assertEquals(1, response.getSelectedShipments().size());
        assertEquals("B", response.getSelectedShipments().get(0).getName());
    }
}