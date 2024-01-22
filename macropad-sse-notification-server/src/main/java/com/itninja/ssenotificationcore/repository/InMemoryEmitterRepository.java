package com.itninja.ssenotificationcore.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.model.DeviceConnection;


@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryEmitterRepository implements EmitterRepository {

    private final Map<String, DeviceConnection> devicesEmitters = new ConcurrentHashMap<>();

    @Override
    public Set<String> getDevicesNames() {
        return this.devicesEmitters.keySet();
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
