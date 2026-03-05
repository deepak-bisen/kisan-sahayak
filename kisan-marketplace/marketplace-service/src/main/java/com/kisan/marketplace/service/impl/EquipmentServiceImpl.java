package com.kisan.marketplace.service.impl;

import com.kisan.marketplace.client.UserClient;
import com.kisan.marketplace.dto.EquipmentDTO;
import com.kisan.marketplace.dto.UserResponseDTO;
import com.kisan.marketplace.entity.Equipment;
import com.kisan.marketplace.repository.EquipmentRepository;
import com.kisan.marketplace.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserClient userClient;

    @Override
    @Transactional
    public EquipmentDTO addEquipment(EquipmentDTO equipmentDTO) {
        // 1. Synchronous inter-service call to verify the owner exists in User Service
        UserResponseDTO user = userClient.getUserById(equipmentDTO.getOwnerId());

        if (user == null) {
            throw new RuntimeException("Owner not found in the system.");
        }

        // 2. Validate role: Only equipment owners or admins can list items
        if (!"EQUIPMENT_OWNER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Only registered EQUIPMENT_OWNERs can list equipment.");
        }

        // 3. Build equipment and cache location data from the user profile
        Equipment equipment = Equipment.builder()
                .equipmentName(equipmentDTO.getName())
                .description(equipmentDTO.getDescription())
                .category(equipmentDTO.getCategory())
                .hourlyRate(equipmentDTO.getHourlyRate())
                .dailyRate(equipmentDTO.getDailyRate())
                .ownerId(equipmentDTO.getOwnerId())
                .villageName(user.getVillageName()) // Cached for faster local searches
                .district(user.getDistrict())       // Cached for faster local searches
                .isAvailable(true)
                .build();

        return mapToDTO(equipmentRepository.save(equipment));
    }

    @Override
    public EquipmentDTO getEquipmentById(String equipmentId) {
        return mapToDTO(equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId)));
    }

    @Override
    public List<EquipmentDTO> getAllEquipment() {
        return equipmentRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<EquipmentDTO> getEquipmentByOwner(String ownerId) {
        return equipmentRepository.findByOwnerId(ownerId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<EquipmentDTO> searchAvailableByCategory(String category) {
        return equipmentRepository.findByCategoryAndIsAvailableTrue(category).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<EquipmentDTO> searchAvailableByVillage(String villageName) {
        return equipmentRepository.findByVillageNameAndIsAvailableTrue(villageName).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EquipmentDTO updateEquipment(String equipmentId, EquipmentDTO equipmentDTO) {
        Equipment existing = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (equipmentDTO.getName() != null) existing.setEquipmentName(equipmentDTO.getName());
        if (equipmentDTO.getDescription() != null) existing.setDescription(equipmentDTO.getDescription());
        if (equipmentDTO.getHourlyRate() != null) existing.setHourlyRate(equipmentDTO.getHourlyRate());
        if (equipmentDTO.getDailyRate() != null) existing.setDailyRate(equipmentDTO.getDailyRate());
        if (equipmentDTO.getCategory() != null) existing.setCategory(equipmentDTO.getCategory());

        // Use object Boolean check to allow updating the boolean flag safely
        existing.setAvailable(equipmentDTO.isAvailable());

        return mapToDTO(equipmentRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteEquipment(String equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new RuntimeException("Equipment not found");
        }
        equipmentRepository.deleteById(equipmentId);
    }

    private EquipmentDTO mapToDTO(Equipment equipment) {
        return EquipmentDTO.builder()
                .equipmentId(equipment.getEquipmentId())
                .name(equipment.getEquipmentName())
                .description(equipment.getDescription())
                .category(equipment.getCategory())
                .hourlyRate(equipment.getHourlyRate())
                .dailyRate(equipment.getDailyRate())
                .ownerId(equipment.getOwnerId())
                .villageName(equipment.getVillageName())
                .district(equipment.getDistrict())
                .isAvailable(equipment.isAvailable())
                .build();
    }
}
