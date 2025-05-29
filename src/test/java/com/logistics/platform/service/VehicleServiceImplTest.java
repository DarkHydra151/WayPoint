package com.logistics.platform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.logistics.platform.domain.VehicleDTO;
import com.logistics.platform.entity.UserEntity;
import com.logistics.platform.entity.VehicleEntity;
import com.logistics.platform.exception.UserNotFoundException;
import com.logistics.platform.exception.VehicleNotFoundException;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.repository.VehicleRepository;
import com.logistics.platform.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private UserRepository userRepository;

    private VehicleServiceImpl vehicleService;

    private UUID vehicleId, driverId;
    private VehicleEntity vehicleEntity;
    private UserEntity driver;
    private VehicleDTO vehicleDTO;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleServiceImpl(vehicleRepository, userRepository);
        vehicleId = UUID.randomUUID();
        driverId = UUID.randomUUID();

        driver = UserEntity.builder()
                .id(driverId)
                .email("driver@example.com")
                .username("driverUser")
                .build();

        vehicleEntity = VehicleEntity.builder()
                .id(vehicleId)
                .driver(driver)
                .type("TRUCK")
                .licensePlate("ABC-123")
                .capacity(5000.0f)
                .currentLocation("Warehouse A")
                .build();

        vehicleDTO = VehicleDTO.builder()
                .id(vehicleId)
                .driverId(driverId)
                .type("TRUCK")
                .licensePlate("ABC-123")
                .capacity(5000.0f)
                .currentLocation("Warehouse A")
                .build();
    }

    @Test
    void testFindAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicleEntity));

        List<VehicleDTO> result = vehicleService.findAllVehicles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TRUCK", result.get(0).getType());
        verify(vehicleRepository).findAll();
    }

    @Test
    void testCreateVehicle() {
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);

        VehicleDTO result = vehicleService.createVehicle(vehicleDTO);

        assertNotNull(result);
        assertEquals("TRUCK", result.getType());
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    void testFindVehicleById() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleEntity));

        VehicleDTO result = vehicleService.findVehicleById(vehicleId);

        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        verify(vehicleRepository).findById(vehicleId);
    }

    @Test
    void testFindVehicleByIdNotFound() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.findVehicleById(vehicleId));
    }

    @Test
    void testFindVehicleByLicensePlate() {
        when(vehicleRepository.findByLicensePlate("ABC-123")).thenReturn(Optional.of(vehicleEntity));

        VehicleDTO result = vehicleService.findVehicleByLicensePlate("ABC-123");

        assertNotNull(result);
        assertEquals("ABC-123", result.getLicensePlate());
        verify(vehicleRepository).findByLicensePlate("ABC-123");
    }

    @Test
    void testFindVehicleByLicensePlateNotFound() {
        when(vehicleRepository.findByLicensePlate("XYZ-999")).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.findVehicleByLicensePlate("XYZ-999"));
    }

    @Test
    void testAssignDriver() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleEntity));
        when(userRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleDTO result = vehicleService.assignDriver(vehicleId, driverId);

        assertNotNull(result);
        assertEquals(driverId, result.getDriverId());
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    void testAssignDriverNotFound() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.assignDriver(vehicleId, driverId));
    }

    @Test
    void testUpdateVehicle() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleDTO updatedVehicleDTO = vehicleDTO.toBuilder()
                .capacity(6000.0f)
                .build();

        VehicleDTO result = vehicleService.updateVehicle(vehicleId, updatedVehicleDTO);

        assertNotNull(result);
        assertEquals(6000.0f, result.getCapacity());
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    void testDeleteVehicle() {
        when(vehicleRepository.existsById(vehicleId)).thenReturn(true);

        vehicleService.deleteVehicle(vehicleId);

        verify(vehicleRepository).deleteById(vehicleId);
    }

    @Test
    void testDeleteVehicleNotFound() {
        when(vehicleRepository.existsById(vehicleId)).thenReturn(false);

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.deleteVehicle(vehicleId));
    }
}
