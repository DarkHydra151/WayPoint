package com.logistics.platform.service;

import com.logistics.platform.domain.WarehouseDTO;
import com.logistics.platform.exception.WarehouseNotFoundException;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {
    List<WarehouseDTO> findAllWarehouses();
    WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO);
    WarehouseDTO findWarehouseById(UUID id) throws WarehouseNotFoundException;
    WarehouseDTO findWarehouseByLocation(String location) throws WarehouseNotFoundException;
    WarehouseDTO updateWarehouse(UUID id, WarehouseDTO updatedWarehouse) throws WarehouseNotFoundException;
    void deleteWarehouse(UUID id) throws WarehouseNotFoundException;
}
