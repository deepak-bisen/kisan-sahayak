package com.kisan.marketplace.service;

import com.kisan.marketplace.dto.EquipmentDTO;

import java.util.List;

public interface EquipmentService {
    EquipmentDTO addEquipment(EquipmentDTO equipmentDTO);
    EquipmentDTO getEquipmentById(String equipmentId);
    List<EquipmentDTO> getAllEquipment();
    List<EquipmentDTO> getEquipmentByOwner(String ownerId);
    List<EquipmentDTO> searchAvailableByCategory(String category);
    List<EquipmentDTO> searchAvailableByVillage(String villageName);
    EquipmentDTO updateEquipment(String equipmentId, EquipmentDTO equipmentDTO);
    void deleteEquipment(String equipmentId);
}
