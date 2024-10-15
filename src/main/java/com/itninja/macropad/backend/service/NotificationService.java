package com.itninja.macropad.backend.service;


import java.util.List;

import com.itninja.macropad.backend.notification.model.SseNotificationDTO;


public interface NotificationService {

    void sendNotifications(List<String> deviceIds, SseNotificationDTO event);
}
