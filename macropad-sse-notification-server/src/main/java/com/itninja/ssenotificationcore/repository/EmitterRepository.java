package com.itninja.ssenotificationcore.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.model.DeviceType;

public interface EmitterRepository {

    void addOrReplaceEmitter(String username, SseEmitter emitter, DeviceType deviceType);

    void remove(String username);

    Optional<SseEmitter> getEmitter(String username);

    Set<String> getDevicesNames();

    Set<String> getMacropadsNames();
}
