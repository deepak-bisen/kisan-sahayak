package com.kisan.marketplace.repository;

import com.kisan.marketplace.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment,String> {

    // Find all equipment owned by a specific user
    List<Equipment> findByOwnerId(String ownerId);

    // Search for available equipment by category (e.g., TRACTOR)
    List<Equipment> findByCategoryAndIsAvailableTrue(String category);

    // Search for available equipment in a specific village
    List<Equipment> findByVillageNameAndIsAvailableTrue(String villageName);
}
