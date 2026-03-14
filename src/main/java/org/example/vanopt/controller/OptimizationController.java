package org.example.vanopt.controller;

import org.example.vanopt.dto.OptimizationRequestDTO;
import org.example.vanopt.dto.OptimizationResponseDTO;
import org.example.vanopt.repository.OptimizationRequestRepository;
import org.example.vanopt.service.OptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

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
    public ResponseEntity<OptimizationResponseDTO> optimizeVan(@Valid @RequestBody OptimizationRequestDTO requestDTO) {
        OptimizationResponseDTO response = optimizationService.optimize(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<OptimizationResponseDTO> getOptimizationById(@PathVariable String requestId) {
        return optimizationRequestRepository.findById(requestId)
                .map(optimizationService::buildResponseFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<OptimizationResponseDTO> getAllOptimizations() {
        return optimizationRequestRepository.findAll()
                .stream()
                .map(optimizationService::buildResponseFromEntity)
                .toList();
    }
}