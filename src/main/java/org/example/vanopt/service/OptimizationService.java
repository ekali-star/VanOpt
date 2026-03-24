package org.example.vanopt.service;

import jakarta.transaction.Transactional;
import org.example.vanopt.algorithm.KnapsackSolver;
import org.example.vanopt.dto.OptimizationRequestDTO;
import org.example.vanopt.dto.OptimizationResponseDTO;
import org.example.vanopt.dto.ShipmentDTO;
import org.example.vanopt.entity.OptimizationRequest;
import org.example.vanopt.entity.Shipment;
import org.example.vanopt.repository.OptimizationRequestRepository;
import org.example.vanopt.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptimizationService {
    private final OptimizationRequestRepository optimizationRequestRepository;
    private final ShipmentRepository shipmentRepository;

    public OptimizationService(OptimizationRequestRepository optimizationRequestRepository, ShipmentRepository shipmentRepository) {
        this.optimizationRequestRepository = optimizationRequestRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public OptimizationResponseDTO optimize(OptimizationRequestDTO requestDTO) {
        OptimizationRequest request = OptimizationRequest.createNew(requestDTO.getMaxVolume());

        List<ShipmentDTO> selectedShipmentsDTO = KnapsackSolver.solve(
                requestDTO.getMaxVolume(),
                requestDTO.getAvailableShipments()
        );

        List<Shipment> allShipments = new ArrayList<>();
        int totalVolume = 0;
        int totalRevenue = 0;

        for (ShipmentDTO sDTO : requestDTO.getAvailableShipments()) {
            boolean isSelected = selectedShipmentsDTO.contains(sDTO);
            if (isSelected) {
                totalVolume += sDTO.getVolume();
                totalRevenue += sDTO.getRevenue();
            }

            Shipment shipment = Shipment.builder()
                    .name(sDTO.getName())
                    .volume(sDTO.getVolume())
                    .revenue(sDTO.getRevenue())
                    .selected(isSelected)
                    .request(request)
                    .build();
            allShipments.add(shipment);
        }

        request.setTotalVolume(totalVolume);
        request.setTotalRevenue(totalRevenue);
        request.setShipments(allShipments);

        optimizationRequestRepository.save(request);

        OptimizationResponseDTO responseDTO = new OptimizationResponseDTO();
        responseDTO.setRequestId(request.getId());
        responseDTO.setCreatedAt(request.getCreatedAt());
        responseDTO.setTotalVolume(totalVolume);
        responseDTO.setTotalRevenue(totalRevenue);

        List<ShipmentDTO> responseShipments = new ArrayList<>();
        for (Shipment s : allShipments) {
            if (s.isSelected()) {
                responseShipments.add(new ShipmentDTO(s.getName(), s.getVolume(), s.getRevenue()));
            }
        }
        responseDTO.setSelectedShipments(responseShipments);

        List<Shipment> unusedShipments = shipmentRepository.findAll().stream()
                .filter(s -> !s.isSelected())
                .collect(Collectors.toList());

        int v = requestDTO.getMaxVolume() - totalVolume;


        List<ShipmentDTO> additionalShipments = KnapsackSolver.solve(v, unusedShipments.stream()
                .map(s -> new ShipmentDTO(s.getName(), s.getVolume(), s.getRevenue()))
                .collect(Collectors.toList()));

        int additionalVolume = additionalShipments.stream().mapToInt(ShipmentDTO::getVolume).sum();
        int additionalRevenue = additionalShipments.stream().mapToInt(ShipmentDTO::getRevenue).sum();

        List<Shipment> additionalShipmentsEntities = unusedShipments.stream()
                .filter(s -> additionalShipments.contains(new ShipmentDTO(s.getName(), s.getVolume(), s.getRevenue())))
                .collect(Collectors.toList());

        for(Shipment s : additionalShipmentsEntities){
            s.setSelected(true);
        }

        responseDTO.getSelectedShipments().addAll(additionalShipments);
        responseDTO.setTotalVolume(totalVolume+additionalVolume);
        responseDTO.setTotalRevenue(totalRevenue+additionalRevenue);
        return responseDTO;
    }
    public OptimizationResponseDTO buildResponseFromEntity(OptimizationRequest request) {
        OptimizationResponseDTO responseDTO = new OptimizationResponseDTO();
        responseDTO.setRequestId(request.getId());
        responseDTO.setCreatedAt(request.getCreatedAt());
        responseDTO.setTotalVolume(request.getTotalVolume());
        responseDTO.setTotalRevenue(request.getTotalRevenue());

        List<ShipmentDTO> selectedShipments = request.getShipments().stream()
                .filter(Shipment::isSelected)
                .map(s -> new ShipmentDTO(s.getName(), s.getVolume(), s.getRevenue()))
                .collect(Collectors.toList());

        responseDTO.setSelectedShipments(selectedShipments);
        return responseDTO;
    }
}
