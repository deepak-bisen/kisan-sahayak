package com.kisan.marketplace.controller;

import com.kisan.marketplace.dto.NotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/marketplace/notifications")
public interface NotificationController {

    @GetMapping("/user/{userId}")
    ResponseEntity<List<NotificationDTO>> getByUser(@PathVariable String userId);

    @GetMapping("/user/{userId}/unread")
    ResponseEntity<List<NotificationDTO>> getUnread(@PathVariable String userId);

    @GetMapping("/user/{userId}/unread/count")
    ResponseEntity<Long> getUnreadCount(@PathVariable String userId);

    @PatchMapping("/{id}/read")
    ResponseEntity<Void> markAsRead(@PathVariable String id);

    @PatchMapping("/user/{userId}/read-all")
    ResponseEntity<Void> markAllAsRead(@PathVariable String userId);
}
