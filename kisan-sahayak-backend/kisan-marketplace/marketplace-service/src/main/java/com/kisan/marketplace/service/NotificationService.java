package com.kisan.marketplace.service;

import com.kisan.marketplace.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    NotificationDTO create(String userId, String type, String message, String relatedId);

    List<NotificationDTO> getByUser(String userId);

    List<NotificationDTO> getUnread(String userId);

    long getUnreadCount(String userId);

    void markAsRead(String id);

    void markAllAsRead(String userId);

    void deleteByUser(String userId);
}
