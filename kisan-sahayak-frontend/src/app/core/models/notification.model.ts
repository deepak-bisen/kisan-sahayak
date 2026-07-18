export interface NotificationDTO {
  id: string;
  userId: string;
  type: string;
  message: string;
  relatedId?: string;
  isRead: boolean;
  createdAt: string;
}
