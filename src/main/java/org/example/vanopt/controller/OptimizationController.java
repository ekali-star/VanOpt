package org.example.vanopt.controller;

import org.example.vanopt.dto.OptimizationRequestDTO;
import org.example.vanopt.dto.OptimizationResponseDTO;
import org.example.vanopt.entity.OptimizationRequest;
import org.example.vanopt.repository.OptimizationRequestRepository;
import org.example.vanopt.service.OptimizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/van-opt")
public class OptimizationController {

    private final OptimizationService optimizationService;
    private final OptimizationRequestRepository optimizationRequestRepository;

    public OptimizationController(OptimizationService optimizationService,
                                  OptimizationRequestRepository optimizationRequestRepository) {
        this.optimizationService = optimizationService;
        this.optimizationRequestRepository = optimizationRequestRepository;
    }

    @PostMapping("/optimize")
    public ResponseEntity<?> optimizeVan(@Valid @RequestBody OptimizationRequestDTO requestDTO) {
        try {
            OptimizationResponseDTO response = optimizationService.optimize(requestDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected server error"));
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getOptimizationById(@PathVariable String requestId) {
        return optimizationRequestRepository.findById(requestId)
                .map(request -> ResponseEntity.ok((Object) optimizationService.buildResponseFromEntity(request)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((Object) Map.of("error", "Optimization request not found")));
    }

    @GetMapping
    public ResponseEntity<List<OptimizationResponseDTO>> getAllOptimizations() {
        List<OptimizationResponseDTO> responseList = optimizationRequestRepository.findAll()
                .stream()
                .map(optimizationService::buildResponseFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }
}