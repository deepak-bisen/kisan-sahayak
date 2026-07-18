package com.kisan.marketplace.controller.impl;

import com.kisan.marketplace.controller.NotificationController;
import com.kisan.marketplace.dto.NotificationDTO;
import com.kisan.marketplace.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;

    @Override
    public ResponseEntity<List<NotificationDTO>> getByUser(String userId) {
        return ResponseEntity.ok(notificationService.getByUser(userId));
    }

    @Override
    public ResponseEntity<List<NotificationDTO>> getUnread(String userId) {
        return ResponseEntity.ok(notificationService.getUnread(userId));
    }

    @Override
    public ResponseEntity<Long> getUnreadCount(String userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @Override
    public ResponseEntity<Void> markAsRead(String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> markAllAsRead(String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}
