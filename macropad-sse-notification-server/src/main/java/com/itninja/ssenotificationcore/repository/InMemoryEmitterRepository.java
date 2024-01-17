package com.itninja.ssenotificationcore.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.model.DeviceConnection;
import com.itninja.ssenotificationcore.notification.model.SseNotificationDTO;


@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryEmitterRepository implements EmitterRepository {

    private final Map<String, DeviceConnection> devicesEmitters = new ConcurrentHashMap<>();

    public Map<String, DeviceConnection> getDevicesEmitters() { return this.devicesEmitters; }

    public void addToBuffer(String deviceId, SseNotificationDTO notificationDTO) {
        Optional.ofNullable(devicesEmitters.get(deviceId))
                .ifPresentOrElse(deviceConnection -> registerInBuffer(notificationDTO, deviceConnection),
                        () -> devicesEmitters.put(deviceId, createConnection(notificationDTO)));
    }

    private void registerInBuffer(SseNotificationDTO notificationDTO, DeviceConnection deviceConnection) {
        deviceConnection.getNotificationsToSend().add(notificationDTO);
    }

    @Override
    public void addOrReplaceEmitter(String deviceId, SseEmitter emitter) {
        log.info("REGISTERING:" + deviceId);
        getConnection(deviceId)
                        .ifPresentOrElse(connection -> updateConnectionWithEmitter(connection, emitter),
                                () -> devicesEmitters.put(deviceId, createConnection(emitter)));
    }

    private DeviceConnection createConnection(SseEmitter emitter) {
        return DeviceConnection.builder()
                .emitter(emitter)
                .build();
    }

    private DeviceConnection createConnection(SseNotificationDTO notificationDTO) {
        List<SseNotificationDTO> notifications = new ArrayList<>();
        notifications.add(notificationDTO);
        return DeviceConnection.builder()
                .notificationsToSend(notifications)
                .build();
    }

    private void updateConnectionWithEmitter(DeviceConnection connection,
                                             SseEmitter emitter) {
        connection.setEmitter(emitter);
    }

    @Override
    public void remove(String deviceId) {
        log.info("REMOVING:" + deviceId);
        if (devicesEmitters.containsKey(deviceId)) {
            log.debug("Removing emitter for user: {}", deviceId);
            getConnection(deviceId)
                    .ifPresent(deviceConnection -> deviceConnection.setEmitter(null));
        } else {
            log.debug("No emitter to remove for user: {}", deviceId);
        }
    }

    @Override
    public Optional<SseEmitter> getEmitter(String deviceId) {
        return Optional.ofNullable(devicesEmitters.get(deviceId))
                .map(DeviceConnection::getEmitter);
    }

    public Optional<DeviceConnection> getConnection(String deviceId) {
        return Optional.ofNullable(devicesEmitters.get(deviceId));
    }
}
