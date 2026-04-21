package com.kisan.knowledge.repository;

import com.kisan.knowledge.entity.CropGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropGuideRepository extends JpaRepository<CropGuide,Integer> {

    // Check if a crop guide already exists by name
    boolean existsByCropNameIgnoreCase(String cropName);

    // Search for guides containing a specific crop name (case-insensitive)
    List<CropGuide> findByCropNameContainingIgnoreCase(String cropName);

    // Search for guides for a specific season (e.g., Rabi, Kharif)
    List<CropGuide> findBySeasonIgnoreCase(String season);
}
