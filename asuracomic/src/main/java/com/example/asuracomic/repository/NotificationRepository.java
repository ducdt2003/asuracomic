package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.link LIKE %:slug%")
    void deleteByLinkContaining(String slug);
}