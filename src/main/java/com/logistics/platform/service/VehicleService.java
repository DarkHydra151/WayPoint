package com.logistics.platform.service;

import com.logistics.platform.domain.VehicleDTO;
import com.logistics.platform.exception.VehicleNotFoundException;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    List<VehicleDTO> findAllVehicles();
    VehicleDTO createVehicle(VehicleDTO vehicleDTO);
    VehicleDTO findVehicleById(UUID id) throws VehicleNotFoundException;
    VehicleDTO findVehicleByLicensePlate(String plate) throws VehicleNotFoundException;
    VehicleDTO assignDriver(UUID vehicleId, UUID driverId) throws VehicleNotFoundException;
    VehicleDTO updateVehicle(UUID id, VehicleDTO updatedVehicle) throws VehicleNotFoundException;
    void deleteVehicle(UUID id) throws VehicleNotFoundException;
}
