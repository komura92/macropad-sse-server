package com.itninja.macropad.backend.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.macropad.backend.model.DeviceConnection;
import com.itninja.macropad.backend.model.DeviceType;


@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryEmitterRepository implements EmitterRepository {

    private final Map<String, DeviceConnection> devicesEmitters = new ConcurrentHashMap<>();

    @Override
    public Set<String> getDevicesNames() {
        return getIdentifiersByType(DeviceType.PC);
    }

    @Override
    public Set<String> getMacropadsNames() {
        return getIdentifiersByType(DeviceType.MACROPAD);
    }

    private Set<String> getIdentifiersByType(DeviceType macropad) {
        return this.devicesEmitters.entrySet().stream()
                .filter(entry -> macropad.equals(entry.getValue().getDeviceType()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public void addOrReplaceEmitter(String deviceId, SseEmitter emitter, DeviceType deviceType) {
        log.info("REGISTERING:" + deviceType.name() + ":" + deviceId);
        getConnection(deviceId)
                .ifPresentOrElse(connection -> updateConnectionWithEmitter(connection, emitter),
                        () -> devicesEmitters.put(deviceId, createConnection(emitter, deviceType)));
    }

    private DeviceConnection createConnection(SseEmitter emitter, DeviceType deviceType) {
        return DeviceConnection.builder()
                .emitter(emitter)
                .deviceType(deviceType)
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
