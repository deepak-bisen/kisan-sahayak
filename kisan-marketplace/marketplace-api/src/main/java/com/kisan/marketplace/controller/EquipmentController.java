package com.kisan.marketplace.controller;

import com.kisan.marketplace.dto.EquipmentDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/marketplace/api/equipment")
public interface EquipmentController {

    @PostMapping
    ResponseEntity<EquipmentDTO> addEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO);

    @GetMapping("/{id}")
    ResponseEntity<EquipmentDTO> getEquipment(@PathVariable("id") String id);

    @GetMapping
    ResponseEntity<List<EquipmentDTO>> getAllEquipment();

    @GetMapping("/owner/{ownerId}")
    ResponseEntity<List<EquipmentDTO>> getEquipmentByOwner(@PathVariable("ownerId") String ownerId);

    @GetMapping("/search/category/{category}")
    ResponseEntity<List<EquipmentDTO>> searchByCategory(@PathVariable("category") String category);

    @GetMapping("/search/village/{villageName}")
    ResponseEntity<List<EquipmentDTO>> searchByVillage(@PathVariable("villageName") String villageName);

    @PutMapping("/{id}")
    ResponseEntity<EquipmentDTO> updateEquipment(@PathVariable("id") String id, @RequestBody EquipmentDTO equipmentDTO);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEquipment(@PathVariable("id") String id);
}