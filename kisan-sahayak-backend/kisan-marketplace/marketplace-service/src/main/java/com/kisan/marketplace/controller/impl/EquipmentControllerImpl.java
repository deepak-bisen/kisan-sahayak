package com.kisan.marketplace.controller.impl;

import com.kisan.marketplace.controller.EquipmentController;
import com.kisan.marketplace.dto.EquipmentDTO;
import com.kisan.marketplace.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace/equipment")
@RequiredArgsConstructor
public class EquipmentControllerImpl implements EquipmentController {

    private final EquipmentService equipmentService;

//    @PostMapping
//    public ResponseEntity<EquipmentDTO> addEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO) {
//        return new ResponseEntity<>(equipmentService.addEquipment(equipmentDTO), HttpStatus.CREATED);
//    }

    @Override
    public ResponseEntity<EquipmentDTO> addEquipment(EquipmentDTO equipmentDTO, MultipartFile image) {
        return new ResponseEntity<>(equipmentService.addEquipment(equipmentDTO, image), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<EquipmentDTO> getEquipment(String id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @Override
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @Override
    public ResponseEntity<List<EquipmentDTO>> getEquipmentByOwner(String ownerId) {
        return ResponseEntity.ok(equipmentService.getEquipmentByOwner(ownerId));
    }

    @Override
    public ResponseEntity<List<EquipmentDTO>> searchByCategory(String category) {
        return ResponseEntity.ok(equipmentService.searchAvailableByCategory(category));
    }

    @Override
    public ResponseEntity<List<EquipmentDTO>> searchByVillage(String villageName) {
        return ResponseEntity.ok(equipmentService.searchAvailableByVillage(villageName));
    }

    @Override
    public ResponseEntity<EquipmentDTO> updateEquipment(String id, EquipmentDTO equipmentDTO) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, equipmentDTO));
    }

    @Override
    public ResponseEntity<Void> deleteEquipment(String id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteEquipmentByOwner(String ownerId) {
        equipmentService.deleteEquipmentByOwner(ownerId);
        return ResponseEntity.noContent().build();
    }
}
