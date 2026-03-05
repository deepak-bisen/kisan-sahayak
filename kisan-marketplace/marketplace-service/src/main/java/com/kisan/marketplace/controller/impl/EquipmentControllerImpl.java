package com.kisan.marketplace.controller.impl;

import com.kisan.marketplace.controller.EquipmentController;
import com.kisan.marketplace.dto.EquipmentDTO;
import com.kisan.marketplace.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentControllerImpl implements EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<EquipmentDTO> addEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO) {
        return new ResponseEntity<>(equipmentService.addEquipment(equipmentDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> getEquipment(@PathVariable String id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<EquipmentDTO>> getEquipmentByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(equipmentService.getEquipmentByOwner(ownerId));
    }

    @GetMapping("/search/category/{category}")
    public ResponseEntity<List<EquipmentDTO>> searchByCategory(@PathVariable String category) {
        return ResponseEntity.ok(equipmentService.searchAvailableByCategory(category));
    }

    @GetMapping("/search/village/{villageName}")
    public ResponseEntity<List<EquipmentDTO>> searchByVillage(@PathVariable String villageName) {
        return ResponseEntity.ok(equipmentService.searchAvailableByVillage(villageName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDTO> updateEquipment(@PathVariable String id, @RequestBody EquipmentDTO equipmentDTO) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, equipmentDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable String id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
