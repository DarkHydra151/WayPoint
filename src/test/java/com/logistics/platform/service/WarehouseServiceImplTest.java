package com.logistics.platform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.logistics.platform.domain.WarehouseDTO;
import com.logistics.platform.entity.UserEntity;
import com.logistics.platform.entity.WarehouseEntity;
import com.logistics.platform.exception.UserNotFoundException;
import com.logistics.platform.exception.WarehouseNotFoundException;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.repository.WarehouseRepository;
import com.logistics.platform.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private UserRepository userRepository;

    private WarehouseServiceImpl warehouseService;

    private UUID warehouseId, managerId;
    private WarehouseEntity warehouseEntity;
    private UserEntity manager;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void setUp() {
        warehouseService = new WarehouseServiceImpl(warehouseRepository, userRepository);
        warehouseId = UUID.randomUUID();
        managerId = UUID.randomUUID();

        manager = UserEntity.builder()
                .id(managerId)
                .email("manager@example.com")
                .username("warehouseManager")
                .build();

        warehouseEntity = WarehouseEntity.builder()
                .id(warehouseId)
                .location("Downtown Storage")
                .capacity(1000)
                .availableSpace(500)
                .manager(manager)
                .build();

        warehouseDTO = WarehouseDTO.builder()
                .id(warehouseId)
                .location("Downtown Storage")
                .capacity(1000)
                .availableSpace(500)
                .managerId(managerId)
                .build();
    }

    @Test
    void testFindAllWarehouses() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouseEntity));

        List<WarehouseDTO> result = warehouseService.findAllWarehouses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Downtown Storage", result.get(0).getLocation());
        verify(warehouseRepository).findAll();
    }

    @Test
    void testCreateWarehouse() {
        when(userRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(warehouseRepository.save(any(WarehouseEntity.class))).thenReturn(warehouseEntity);

        WarehouseDTO result = warehouseService.createWarehouse(warehouseDTO);

        assertNotNull(result);
        assertEquals("Downtown Storage", result.getLocation());
        verify(warehouseRepository).save(any(WarehouseEntity.class));
    }

    @Test
    void testCreateWarehouseManagerNotFound() {
        when(userRepository.findById(managerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> warehouseService.createWarehouse(warehouseDTO));
    }

    @Test
    void testFindWarehouseById() {
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouseEntity));

        WarehouseDTO result = warehouseService.findWarehouseById(warehouseId);

        assertNotNull(result);
        assertEquals(warehouseId, result.getId());
        verify(warehouseRepository).findById(warehouseId);
    }

    @Test
    void testFindWarehouseByIdNotFound() {
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.findWarehouseById(warehouseId));
    }

    @Test
    void testFindWarehouseByLocation() {
        when(warehouseRepository.findByLocation("Downtown Storage")).thenReturn(Optional.of(warehouseEntity));

        WarehouseDTO result = warehouseService.findWarehouseByLocation("Downtown Storage");

        assertNotNull(result);
        assertEquals("Downtown Storage", result.getLocation());
        verify(warehouseRepository).findByLocation("Downtown Storage");
    }

    @Test
    void testFindWarehouseByLocationNotFound() {
        when(warehouseRepository.findByLocation("Unknown")).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.findWarehouseByLocation("Unknown"));
    }

    @Test
    void testUpdateWarehouse() {
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouseEntity));
        when(warehouseRepository.save(any(WarehouseEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WarehouseDTO updatedWarehouseDTO = warehouseDTO.toBuilder()
                .availableSpace(750)
                .build();

        WarehouseDTO result = warehouseService.updateWarehouse(warehouseId, updatedWarehouseDTO);

        assertNotNull(result);
        assertEquals(750, result.getAvailableSpace());
        verify(warehouseRepository).save(any(WarehouseEntity.class));
    }

    @Test
    void testDeleteWarehouse() {
        when(warehouseRepository.existsById(warehouseId)).thenReturn(true);

        warehouseService.deleteWarehouse(warehouseId);

        verify(warehouseRepository).deleteById(warehouseId);
    }

    @Test
    void testDeleteWarehouseNotFound() {
        when(warehouseRepository.existsById(warehouseId)).thenReturn(false);

        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.deleteWarehouse(warehouseId));
    }
}
