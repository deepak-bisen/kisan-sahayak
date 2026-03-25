package com.kisan.knowledge.service.impl;

import com.kisan.knowledge.dto.CropGuideDTO;
import com.kisan.knowledge.entity.CropGuide;
import com.kisan.knowledge.repository.CropGuideRepository;
import com.kisan.knowledge.service.CropGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CropGuideServiceImpl implements CropGuideService {

    @Autowired
    private CropGuideRepository cropGuideRepository;


    @Override
    @Transactional
    public CropGuideDTO addCropGuide(CropGuideDTO cropGuideDTO) {
        if (cropGuideRepository.existsByCropNameIgnoreCase(cropGuideDTO.getCropName())) {
             throw new RuntimeException("A guide for the crop already exists");
        }
        CropGuide cropGuide = CropGuide.builder()
                .cropName(cropGuideDTO.getCropName())
                .season(cropGuideDTO.getSeason())
                .soilType(cropGuideDTO.getSoilType())
                .durationInDays(cropGuideDTO.getDurationInDays())
                .bestPractices(cropGuideDTO.getBestPractices())
                .diseaseManagement(cropGuideDTO.getDiseaseManagement())
                .build();
        return mapToDTO(cropGuideRepository.save(cropGuide));
    }

    @Override
    public CropGuideDTO getCropGuideById(String guideId) {
        CropGuide cropGuide = cropGuideRepository.findById(Integer.valueOf(guideId))
                .orElseThrow(() -> new RuntimeException("Crop Guide not found with id: "+ guideId));
        return mapToDTO(cropGuide);
    }

    @Override
    public List<CropGuideDTO> getAllCropGuides() {
        return cropGuideRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CropGuideDTO> searchByCropName(String cropName) {
        return cropGuideRepository.findByCropNameContainingIgnoreCase(cropName).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CropGuideDTO> searchBySeason(String season) {
        return cropGuideRepository.findBySeasonIgnoreCase(season).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public CropGuideDTO updateCropGuide(String guideId, CropGuideDTO cropGuideDTO) {

        CropGuide existingGuide = cropGuideRepository.findById(Integer.valueOf(guideId))
                .orElseThrow(()-> new RuntimeException("Crop Guide not found with id: " + guideId));

        if (cropGuideDTO.getCropName() != null) existingGuide.setCropName(cropGuideDTO.getCropName());
        if (cropGuideDTO.getSeason() != null) existingGuide.setSeason(cropGuideDTO.getSeason());
        if (cropGuideDTO.getSoilType() != null) existingGuide.setSoilType(cropGuideDTO.getSoilType());
        if (cropGuideDTO.getDurationInDays() != null) existingGuide.setDurationInDays(cropGuideDTO.getDurationInDays());
        if (cropGuideDTO.getBestPractices() != null) existingGuide.setBestPractices(cropGuideDTO.getBestPractices());
        if (cropGuideDTO.getDiseaseManagement() != null) existingGuide.setDiseaseManagement(cropGuideDTO.getDiseaseManagement());

        return mapToDTO(cropGuideRepository.save(existingGuide));
    }

    @Override
    @Transactional
    public void deleteCropGuide(String guideId) {
        if (cropGuideRepository.existsById(Integer.valueOf(guideId))){
            throw new RuntimeException("Crop Guide not found with id: " + guideId);
        }
       cropGuideRepository.deleteById(Integer.valueOf(guideId));
    }

    private  CropGuideDTO mapToDTO(CropGuide cropGuide){
        return CropGuideDTO.builder()
                .guideId(cropGuide.getGuideId())
                .cropName(cropGuide.getCropName())
                .season(cropGuide.getSeason())
                .soilType(cropGuide.getSoilType())
                .durationInDays(cropGuide.getDurationInDays())
                .bestPractices(cropGuide.getBestPractices())
                .diseaseManagement(cropGuide.getDiseaseManagement())
                .build();
    }
}
