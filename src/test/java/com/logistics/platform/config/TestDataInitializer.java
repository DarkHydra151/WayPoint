package com.logistics.platform.config;

import com.logistics.platform.domain.enums.Role;
import com.logistics.platform.entity.*;
import com.logistics.platform.repository.*;
import com.logistics.platform.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataInitializer {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final PackageRepository packageRepository;
    private final WarehouseRepository warehouseRepository;
    private final RouteRepository routeRepository;

    public TestData initTestData() {
        packageRepository.deleteAll();
        routeRepository.deleteAll();
        vehicleRepository.deleteAll();
        orderRepository.deleteAll();
        warehouseRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity adminUser = userRepository.save(UserEntity.builder()
                .email("admin@logistics.com")
                .password("securepassword")
                .username("LogisticsAdmin")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build());

        String adminToken = jwtProvider.createToken(adminUser.getEmail(), adminUser.getRole());

        UserEntity clientUser = userRepository.save(UserEntity.builder()
                .email("client@logistics.com")
                .password("securepassword")
                .username("ClientUser")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build());

        String clientToken = jwtProvider.createToken(clientUser.getEmail(), clientUser.getRole());

        WarehouseEntity warehouse = warehouseRepository.save(WarehouseEntity.builder()
                .location("Main Distribution Center")
                .capacity(5000)
                .availableSpace(3000)
                .manager(adminUser)
                .build());

        VehicleEntity vehicle = vehicleRepository.save(VehicleEntity.builder()
                .type("TRUCK")
                .licensePlate("LOG-123")
                .capacity(10000.0f)
                .currentLocation("Warehouse")
                .driver(adminUser)
                .build());

        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .client(clientUser)
                .status("PENDING")
                .origin("Warehouse A")
                .destination("Client Address")
                .estimatedDeliveryTime(LocalDateTime.now().plusDays(2))
                .build());

        PackageEntity packageEntity = packageRepository.save(PackageEntity.builder()
                .order(order)
                .description("Electronics and fragile goods")
                .weight(15.0f)
                .status("PACKED")
                .build());

        RouteEntity route = routeRepository.save(RouteEntity.builder()
                .vehicle(vehicle)
                .origin("Warehouse A")
                .destination("Client Address")
                .estimatedTime("5 hours")
                .trafficConditions("Moderate traffic")
                .build());

        return new TestData(adminUser.getId(), clientUser.getId(), warehouse.getId(), vehicle.getId(), order.getId(), packageEntity.getId(), route.getId(), adminToken, clientToken);
    }

    public record TestData(UUID adminId, UUID clientId, UUID warehouseId, UUID vehicleId, UUID orderId, UUID packageId, UUID routeId, String adminToken, String clientToken) {}
}
