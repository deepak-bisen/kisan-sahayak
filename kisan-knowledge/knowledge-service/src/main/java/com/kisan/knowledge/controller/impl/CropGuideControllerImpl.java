package com.kisan.knowledge.controller.impl;

import com.kisan.knowledge.controller.CropGuideController;
import com.kisan.knowledge.dto.CropGuideDTO;
import com.kisan.knowledge.service.CropGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CropGuideControllerImpl implements CropGuideController {

    private final CropGuideService cropGuideService;

    public CropGuideControllerImpl(CropGuideService cropGuideService) {
        this.cropGuideService = cropGuideService;
    }

    // Typically protected for ADMIN role later
    @Override
    public ResponseEntity<CropGuideDTO> addCropGuide(CropGuideDTO cropGuideDTO) {
        return new ResponseEntity<>(cropGuideService.addCropGuide(cropGuideDTO), HttpStatus. CREATED);
    }

    @Override
    public ResponseEntity<CropGuideDTO> getCropGuide(String id) {
        return ResponseEntity.ok(cropGuideService.getCropGuideById(id));
    }

    @Override
    public ResponseEntity<List<CropGuideDTO>> getAllCropGuides() {
        return ResponseEntity.ok(cropGuideService.getAllCropGuides());
    }

    @Override
    public ResponseEntity<List<CropGuideDTO>> searchByCropName(String cropName) {
        return ResponseEntity.ok(cropGuideService.searchByCropName(cropName));
    }

    @Override
    public ResponseEntity<List<CropGuideDTO>> searchBySeason(String season) {
        return ResponseEntity.ok(cropGuideService.searchBySeason(season));
    }

    // Typically protected for ADMIN role later
    @Override
    public ResponseEntity<CropGuideDTO> updateCropGuide(String id, CropGuideDTO cropGuideDTO) {
        return ResponseEntity.ok(cropGuideService.updateCropGuide(id, cropGuideDTO));
    }

    // Typically protected for ADMIN role later
    @Override
    public ResponseEntity<Void> deleteCropGuide(String id) {
        cropGuideService.deleteCropGuide(id);
        return ResponseEntity.noContent().build();
    }
}
