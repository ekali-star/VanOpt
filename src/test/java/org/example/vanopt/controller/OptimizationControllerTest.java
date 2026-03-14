package org.example.vanopt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vanopt.dto.OptimizationRequestDTO;
import org.example.vanopt.dto.OptimizationResponseDTO;
import org.example.vanopt.dto.ShipmentDTO;
import org.example.vanopt.entity.OptimizationRequest;
import org.example.vanopt.repository.OptimizationRequestRepository;
import org.example.vanopt.service.OptimizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptimizationControllerTest {

    @Mock
    private OptimizationService optimizationService;

    @Mock
    private OptimizationRequestRepository optimizationRequestRepository;

    @InjectMocks
    private OptimizationController optimizationController;

    private OptimizationRequestDTO requestDTO;
    private OptimizationResponseDTO responseDTO;
    private OptimizationRequest optimizationRequest;
    private final String requestId = "test-123";

    @BeforeEach
    void setUp() {
        ShipmentDTO shipment1 = new ShipmentDTO("Package A", 10, 100);
        ShipmentDTO shipment2 = new ShipmentDTO("Package B", 15, 150);

        requestDTO = new OptimizationRequestDTO();
        requestDTO.setMaxVolume(25);
        requestDTO.setAvailableShipments(Arrays.asList(shipment1, shipment2));

        responseDTO = new OptimizationResponseDTO();
        responseDTO.setRequestId(requestId);
        responseDTO.setSelectedShipments(Arrays.asList(shipment1, shipment2));
        responseDTO.setTotalVolume(25);
        responseDTO.setTotalRevenue(250);
        responseDTO.setCreatedAt(Instant.now());

        optimizationRequest = new OptimizationRequest();
        optimizationRequest.setId(requestId);
    }

    @Test
    void optimizeVan_ShouldReturnOptimizationResponse() {
        when(optimizationService.optimize(any(OptimizationRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<OptimizationResponseDTO> response =
                optimizationController.optimizeVan(requestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRequestId()).isEqualTo(requestId);
        verify(optimizationService, times(1)).optimize(requestDTO);
    }

    @Test
    void getOptimizationById_WhenExists_ShouldReturnOptimization() {
        when(optimizationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(optimizationRequest));
        when(optimizationService.buildResponseFromEntity(optimizationRequest))
                .thenReturn(responseDTO);

        ResponseEntity<OptimizationResponseDTO> response =
                optimizationController.getOptimizationById(requestId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRequestId()).isEqualTo(requestId);
        verify(optimizationRequestRepository, times(1)).findById(requestId);
        verify(optimizationService, times(1)).buildResponseFromEntity(optimizationRequest);
    }

    @Test
    void getOptimizationById_WhenNotExists_ShouldReturnNotFound() {
        when(optimizationRequestRepository.findById("invalid-id"))
                .thenReturn(Optional.empty());

        ResponseEntity<OptimizationResponseDTO> response =
                optimizationController.getOptimizationById("invalid-id");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(optimizationRequestRepository, times(1)).findById("invalid-id");
        verifyNoInteractions(optimizationService);
    }

    @Test
    void getAllOptimizations_ShouldReturnListOfOptimizations() {
        List<OptimizationRequest> requestList = Arrays.asList(optimizationRequest);
        when(optimizationRequestRepository.findAll()).thenReturn(requestList);
        when(optimizationService.buildResponseFromEntity(optimizationRequest)).thenReturn(responseDTO);

        List<OptimizationResponseDTO> responses = optimizationController.getAllOptimizations();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRequestId()).isEqualTo(requestId);
        verify(optimizationRequestRepository, times(1)).findAll();
        verify(optimizationService, times(1)).buildResponseFromEntity(optimizationRequest);
    }

    @Test
    void getAllOptimizations_WhenEmpty_ShouldReturnEmptyList() {
        when(optimizationRequestRepository.findAll()).thenReturn(List.of());

        List<OptimizationResponseDTO> responses = optimizationController.getAllOptimizations();

        assertThat(responses).isEmpty();
        verify(optimizationRequestRepository, times(1)).findAll();
        verifyNoInteractions(optimizationService);
    }
}