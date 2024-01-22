package com.itninja.ssenotificationcore.service;

import java.io.IOException;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.itninja.ssenotificationcore.mapper.EventMapper;
import com.itninja.ssenotificationcore.notification.model.SseNotificationDTO;
import com.itninja.ssenotificationcore.repository.EmitterRepository;

@Service
@Primary
@AllArgsConstructor
@Slf4j
public class SseNotificationService implements NotificationService {

    private final EmitterRepository emitterRepository;
    private final EventMapper eventMapper;

    @Override
    public void sendNotifications(List<String> deviceIds, SseNotificationDTO event) {
        if (event == null) {
            return;
        }
        deviceIds.forEach(deviceId -> doSendNotification(deviceId, event));
    }

    public void doSendNotification(String deviceId, SseNotificationDTO event) {
        emitterRepository.getEmitter(deviceId)
                .ifPresentOrElse(sseEmitter -> {
                    try {
                        log.debug("Sending event: {} for device: {}", event, deviceId);
                        sseEmitter.send(eventMapper.toSseEventBuilder(event));
                    } catch (IOException | IllegalStateException e) {
                        log.warn("Error while sending event: {} for device: {} - exception:", event, deviceId, e);
                        emitterRepository.remove(deviceId);
                    }
                }, () -> log.warn("No emitter for device {}", deviceId));
    }
}
