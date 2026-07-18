export interface EquipmentDTO {
  equipmentId?: string;
  name: string;
  description?: string;
  category: string;
  hourlyRate: number;
  dailyRate: number;
  ownerId: string;
  ownerName?: string;
  imageUrl?: string;
  villageName?: string;
  district?: string;
  isAvailable?: boolean;
}
