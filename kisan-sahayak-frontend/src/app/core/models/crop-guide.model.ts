export interface CropGuideDTO {
  guideId?: string;
  cropName: string;
  season: string;
  soilType: string;
  durationInDays: number;
  bestPractices: string;
  diseaseManagement?: string;
}
