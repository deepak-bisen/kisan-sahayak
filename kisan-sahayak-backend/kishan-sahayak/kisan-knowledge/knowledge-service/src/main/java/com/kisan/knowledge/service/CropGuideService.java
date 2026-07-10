package com.kisan.knowledge.service;

import com.kisan.knowledge.dto.CropGuideDTO;
import java.util.List;

public interface CropGuideService {
    CropGuideDTO addCropGuide(CropGuideDTO cropGuideDTO);
    CropGuideDTO getCropGuideById(String guideId);
    List<CropGuideDTO> getAllCropGuides();
    List<CropGuideDTO> searchByCropName(String cropName);
    List<CropGuideDTO> searchBySeason(String season);
    CropGuideDTO updateCropGuide(String guideId, CropGuideDTO cropGuideDTO);
    void deleteCropGuide(String guideId);
}
