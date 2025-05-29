package com.logistics.platform.service;

import com.logistics.platform.domain.PackageDTO;
import com.logistics.platform.exception.PackageNotFoundException;

import java.util.List;
import java.util.UUID;

public interface PackageService {
    List<PackageDTO> findAllPackages();
    PackageDTO createPackage(PackageDTO packageDTO);
    PackageDTO findPackageById(UUID id) throws PackageNotFoundException;
    List<PackageDTO> findPackagesByOrder(UUID orderId);
    PackageDTO updatePackage(UUID id, PackageDTO updatedPackage) throws PackageNotFoundException;
    void deletePackage(UUID id) throws PackageNotFoundException;
}
