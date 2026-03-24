package com.kisan.knowledge.controller;

import com.kisan.knowledge.dto.CropGuideDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge/guides")
public interface CropGuideController {

    // Typically protected for ADMIN role later
    @PostMapping
    public ResponseEntity<CropGuideDTO> addCropGuide(@Valid @RequestBody CropGuideDTO cropGuideDTO);

    @GetMapping("/{id}")
    public ResponseEntity<CropGuideDTO> getCropGuide(@PathVariable String id);

    @GetMapping
    public ResponseEntity<List<CropGuideDTO>> getAllCropGuides();

    @GetMapping("/search/crop/{cropName}")
    public ResponseEntity<List<CropGuideDTO>> searchByCropName(@PathVariable String cropName);

    @GetMapping("/search/season/{season}")
    public ResponseEntity<List<CropGuideDTO>> searchBySeason(@PathVariable String season);

    // Typically protected for ADMIN role later
    @PutMapping("/{id}")
    public ResponseEntity<CropGuideDTO> updateCropGuide(@PathVariable String id, @RequestBody CropGuideDTO cropGuideDTO);

    // Typically protected for ADMIN role later
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCropGuide(@PathVariable String id);
}
