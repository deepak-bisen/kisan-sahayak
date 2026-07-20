package com.kisan.marketplace.controller;

import com.kisan.marketplace.dto.EquipmentDTO;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/marketplace/equipment")
public interface EquipmentController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EquipmentDTO> addEquipment(
            @RequestPart("equipment") @Valid EquipmentDTO equipmentDTO,
            @RequestPart(value = "image", required = false) MultipartFile image);

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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EquipmentDTO> updateEquipment(
            @PathVariable("id") String id,
            @RequestPart("equipment") @Valid EquipmentDTO equipmentDTO,
            @RequestPart(value = "image", required = false) MultipartFile image);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEquipment(@PathVariable("id") String id);

    @DeleteMapping("/owner/{ownerId}")
    ResponseEntity<Void> deleteEquipmentByOwner(@PathVariable("ownerId") String ownerId);
}