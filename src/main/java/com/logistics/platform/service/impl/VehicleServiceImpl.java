package com.logistics.platform.service.impl;

import com.logistics.platform.domain.VehicleDTO;
import com.logistics.platform.entity.UserEntity;
import com.logistics.platform.entity.VehicleEntity;
import com.logistics.platform.exception.UserNotFoundException;
import com.logistics.platform.exception.VehicleNotFoundException;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.repository.VehicleRepository;
import com.logistics.platform.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDTO> findAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        VehicleEntity vehicle = VehicleEntity.builder()
                .type(vehicleDTO.getType())
                .licensePlate(vehicleDTO.getLicensePlate())
                .capacity(vehicleDTO.getCapacity())
                .currentLocation(vehicleDTO.getCurrentLocation())
                .build();

        VehicleEntity savedVehicle = vehicleRepository.save(vehicle);
        return mapToDTO(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDTO findVehicleById(UUID id) {
        VehicleEntity vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        return mapToDTO(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDTO findVehicleByLicensePlate(String plate) {
        VehicleEntity vehicle = vehicleRepository.findByLicensePlate(plate)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with license plate: " + plate));
        return mapToDTO(vehicle);
    }

    @Override
    @Transactional
    public VehicleDTO assignDriver(UUID vehicleId, UUID driverId) {
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + vehicleId));

        UserEntity driver = userRepository.findById(driverId)
                .orElseThrow(() -> new UserNotFoundException("Driver not found with id: " + driverId));

        vehicle = vehicle.toBuilder()
                .driver(driver)
                .build();

        VehicleEntity updatedVehicle = vehicleRepository.save(vehicle);
        return mapToDTO(updatedVehicle);
    }

    @Override
    @Transactional
    public VehicleDTO updateVehicle(UUID id, VehicleDTO updatedVehicle) {
        VehicleEntity vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        vehicle = vehicle.toBuilder()
                .type(updatedVehicle.getType())
                .licensePlate(updatedVehicle.getLicensePlate())
                .capacity(updatedVehicle.getCapacity())
                .currentLocation(updatedVehicle.getCurrentLocation())
                .build();

        VehicleEntity savedVehicle = vehicleRepository.save(vehicle);
        return mapToDTO(savedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(UUID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    private VehicleDTO mapToDTO(VehicleEntity vehicle) {
        return VehicleDTO.builder()
                .id(vehicle.getId())
                .driverId(vehicle.getDriver() != null ? vehicle.getDriver().getId() : null)
                .type(vehicle.getType())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .currentLocation(vehicle.getCurrentLocation())
                .build();
    }
}
