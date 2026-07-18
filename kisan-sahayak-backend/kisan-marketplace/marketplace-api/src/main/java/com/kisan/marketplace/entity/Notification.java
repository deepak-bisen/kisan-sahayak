package com.kisan.marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "RELATED_ID")
    private String relatedId;

    @Column(name = "IS_READ", nullable = false)
    private boolean isRead;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}
