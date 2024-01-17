package com.itninja.ssenotificationcore.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.itninja.ssenotificationcore.notification.model.SseNotificationDTO;
import com.itninja.ssenotificationcore.repository.InMemoryEmitterRepository;
import com.itninja.ssenotificationcore.service.SseNotificationService;

@Service
@RequiredArgsConstructor
public class NotificationSenderScheduler {
    private final SseNotificationService notificationService;
    private final InMemoryEmitterRepository emitterRepository;

    private final List<SseNotificationDTO> notificationsToRemove = new ArrayList<>();


    // faster notifications for presentation
//    @Scheduled(fixedDelay = 3000)
    @Scheduled(fixedDelay = 300)
    public void sendNotifications() {
        emitterRepository.getDevicesEmitters().forEach((deviceId, connection) -> {
            if (CollectionUtils.isEmpty(connection.getNotificationsToSend()) ||
                    Objects.isNull(connection.getEmitter()))
                return;

            notificationsToRemove.clear();
            connection.getNotificationsToSend()
                            .forEach(notification -> {
                                notificationService.doSendNotification(deviceId, notification);
                                prepareCleanUpBuffer(notificationsToRemove, deviceId, notification);
                            });
            if (CollectionUtils.isNotEmpty(notificationsToRemove))
                connection.getNotificationsToSend().removeAll(notificationsToRemove);
        });
    }

    private void prepareCleanUpBuffer(List<SseNotificationDTO> notificationsToRemove,
                                      String deviceId,
                                      SseNotificationDTO notification) {
        if (emitterRepository.getEmitter(deviceId).isPresent()) {
            notificationsToRemove.add(notification);
        }
    }
}
