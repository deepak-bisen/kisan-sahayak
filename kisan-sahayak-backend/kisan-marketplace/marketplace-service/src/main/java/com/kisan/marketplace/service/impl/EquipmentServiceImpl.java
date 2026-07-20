package com.kisan.marketplace.service.impl;

import com.kisan.marketplace.client.UserClient;
import com.kisan.marketplace.dto.EquipmentDTO;
import com.kisan.marketplace.dto.UserResponseDTO;
import com.kisan.marketplace.entity.Equipment;
import com.kisan.marketplace.entity.Booking;
import com.kisan.marketplace.repository.BookingRepository;
import com.kisan.marketplace.repository.EquipmentRepository;
import com.kisan.marketplace.repository.NotificationRepository;
import com.kisan.marketplace.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public EquipmentDTO addEquipment(EquipmentDTO equipmentDTO, MultipartFile image) {
        log.info("Equipment listing request received for ownerId={} equipmentName={}", equipmentDTO.getOwnerId(), equipmentDTO.getName());

        UserResponseDTO user;
        try {
            user = userClient.getUserById(equipmentDTO.getOwnerId());
        } catch (Exception e) {
            log.warn("User service unavailable, allowing equipment listing for ownerId={}: {}", equipmentDTO.getOwnerId(), e.getMessage());
            user = null;
        }

        if (user == null) {
            log.warn("Equipment listing failed: owner not found for ownerId={}", equipmentDTO.getOwnerId());
            throw new RuntimeException("Owner not found in the system.");
        }

        //Storing image URLs
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                Files.copy(
                        image.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            imageUrl = "/uploads/" + fileName;
        }

        // 3. Build equipment and cache owner data from the user profile
        Equipment equipment = Equipment.builder()
                .name(equipmentDTO.getName())
                .description(equipmentDTO.getDescription())
                .category(equipmentDTO.getCategory())
                .hourlyRate(equipmentDTO.getHourlyRate())
                .dailyRate(equipmentDTO.getDailyRate())
                .ownerId(equipmentDTO.getOwnerId())
                .ownerName(user.getFullName())
                .imageUrl(imageUrl)
                .villageName(user.getVillageName()) // Cached for faster local searches
                .district(user.getDistrict())       // Cached for faster local searches
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .build();

        Equipment savedEquipment = equipmentRepository.save(equipment);
        log.info("Equipment listed successfully with equipmentId={} ownerId={}", savedEquipment.getId(), equipmentDTO.getOwnerId());
        return mapToDTO(savedEquipment);
    }

    @Override
    public EquipmentDTO getEquipmentById(String equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));
        if (equipment.getOwnerName() == null) {
            try {
                UserResponseDTO owner = userClient.getUserById(equipment.getOwnerId());
                if (owner != null) {
                    equipment.setOwnerName(owner.getFullName());
                    equipmentRepository.save(equipment);
                }
            } catch (Exception e) {
                log.warn("Could not fetch owner name for equipment {}: {}", equipmentId, e.getMessage());
            }
        }
        return mapToDTO(equipment);
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
    public EquipmentDTO updateEquipment(String equipmentId, EquipmentDTO equipmentDTO, MultipartFile image) {
        Equipment existing = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (equipmentDTO.getName() != null) existing.setName(equipmentDTO.getName());
        if (equipmentDTO.getDescription() != null) existing.setDescription(equipmentDTO.getDescription());
        if (equipmentDTO.getHourlyRate() != null) existing.setHourlyRate(equipmentDTO.getHourlyRate());
        if (equipmentDTO.getDailyRate() != null) existing.setDailyRate(equipmentDTO.getDailyRate());
        if (equipmentDTO.getCategory() != null) existing.setCategory(equipmentDTO.getCategory());

        // Handle image replacement
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            existing.setImageUrl("/uploads/" + fileName);
        }

        // Use object Boolean check to allow updating the boolean flag safely
        existing.setAvailable(equipmentDTO.isAvailable());
        existing.setUpdatedAt(LocalDateTime.now());

        return mapToDTO(equipmentRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteEquipment(String equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new RuntimeException("Equipment not found");
        }
        cascadeDeleteBookings(equipmentId);
        equipmentRepository.deleteById(equipmentId);
    }

    @Override
    @Transactional
    public void deleteEquipmentByOwner(String ownerId) {
        List<Equipment> owned = equipmentRepository.findByOwnerId(ownerId);
        if (!owned.isEmpty()) {
            for (Equipment eq : owned) {
                cascadeDeleteBookings(eq.getId());
            }
            equipmentRepository.deleteAll(owned);
            log.info("Deleted {} equipment listings for ownerId={}", owned.size(), ownerId);
        }
    }

    private void cascadeDeleteBookings(String equipmentId) {
        List<Booking> bookings = bookingRepository.findByEquipmentId(equipmentId);
        for (Booking b : bookings) {
            notificationRepository.deleteByRelatedId(b.getId());
        }
        bookingRepository.deleteByEquipmentId(equipmentId);
    }

    private EquipmentDTO mapToDTO(Equipment equipment) {
        return EquipmentDTO.builder()
                .equipmentId(equipment.getId())
                .name(equipment.getName())
                .description(equipment.getDescription())
                .category(equipment.getCategory())
                .hourlyRate(equipment.getHourlyRate())
                .dailyRate(equipment.getDailyRate())
                .ownerId(equipment.getOwnerId())
                .ownerName(equipment.getOwnerName())
                .imageUrl(equipment.getImageUrl())
                .villageName(equipment.getVillageName())
                .district(equipment.getDistrict())
                .isAvailable(equipment.isAvailable())
                .build();
    }
}
