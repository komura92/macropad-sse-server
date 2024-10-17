package com.itninja.macropad.backend.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.macropad.backend.model.DeviceType;

public interface EmitterRepository {

    void addOrReplaceEmitter(String deviceId, SseEmitter emitter, DeviceType deviceType);

    void removeEmitter(String deviceId);

    Optional<SseEmitter> getEmitter(String deviceId);

    Set<String> getDevicesNames();

    Set<String> getMacropadsNames();

    void unregister(String deviceId);
}
