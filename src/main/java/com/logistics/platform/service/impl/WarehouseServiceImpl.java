package com.logistics.platform.service.impl;

import com.logistics.platform.domain.WarehouseDTO;
import com.logistics.platform.entity.UserEntity;
import com.logistics.platform.entity.WarehouseEntity;
import com.logistics.platform.exception.UserNotFoundException;
import com.logistics.platform.exception.WarehouseNotFoundException;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.repository.WarehouseRepository;
import com.logistics.platform.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        UserEntity manager = userRepository.findById(warehouseDTO.getManagerId())
                .orElseThrow(() -> new UserNotFoundException("Manager not found with id: " + warehouseDTO.getManagerId()));

        WarehouseEntity warehouse = WarehouseEntity.builder()
                .location(warehouseDTO.getLocation())
                .capacity(warehouseDTO.getCapacity())
                .availableSpace(warehouseDTO.getAvailableSpace())
                .manager(manager)
                .build();

        WarehouseEntity savedWarehouse = warehouseRepository.save(warehouse);
        return mapToDTO(savedWarehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDTO findWarehouseById(UUID id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found with id: " + id));
        return mapToDTO(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDTO findWarehouseByLocation(String location) {
        WarehouseEntity warehouse = warehouseRepository.findByLocation(location)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found at location: " + location));
        return mapToDTO(warehouse);
    }

    @Override
    @Transactional
    public WarehouseDTO updateWarehouse(UUID id, WarehouseDTO updatedWarehouse) {
        WarehouseEntity warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found with id: " + id));

        warehouse = warehouse.toBuilder()
                .location(updatedWarehouse.getLocation())
                .capacity(updatedWarehouse.getCapacity())
                .availableSpace(updatedWarehouse.getAvailableSpace())
                .build();

        WarehouseEntity savedWarehouse = warehouseRepository.save(warehouse);
        return mapToDTO(savedWarehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(UUID id) {
        if (!warehouseRepository.existsById(id)) {
            throw new WarehouseNotFoundException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    private WarehouseDTO mapToDTO(WarehouseEntity warehouse) {
        return WarehouseDTO.builder()
                .id(warehouse.getId())
                .location(warehouse.getLocation())
                .capacity(warehouse.getCapacity())
                .availableSpace(warehouse.getAvailableSpace())
                .managerId(warehouse.getManager() != null ? warehouse.getManager().getId() : null)
                .build();
    }
}
