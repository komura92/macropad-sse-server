package com.itninja.ssenotificationcore.service;


import java.util.List;

import com.itninja.ssenotificationcore.notification.model.SseNotificationDTO;


public interface NotificationService {

    void sendNotifications(List<String> deviceIds, SseNotificationDTO event);
}
