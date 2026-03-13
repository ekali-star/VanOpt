package org.example.vanopt.repository;

import org.example.vanopt.entity.OptimizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptimizationRequestRepository extends JpaRepository<OptimizationRequest, String> {
}
