export type BookingStatus = 'REQUESTED' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface BookingDTO {
  bookingId?: string;
  equipmentId: string;
  renterId: string;
  startDate: string; // ISO date
  endDate: string; // ISO date
  totalCost?: number;
  status?: BookingStatus;
}
