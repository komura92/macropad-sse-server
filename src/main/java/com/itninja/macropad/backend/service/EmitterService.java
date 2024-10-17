package com.itninja.macropad.backend.service;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.macropad.backend.model.DeviceType;
import com.itninja.macropad.backend.repository.EmitterRepository;

@Service
@Slf4j
public class EmitterService {

    private final long eventsTimeout;
    private final EmitterRepository repository;

    public EmitterService(@Value("${events.connection.timeout}") long eventsTimeout,
                          EmitterRepository repository) {
        this.eventsTimeout = eventsTimeout;
        this.repository = repository;
    }

    public Set<String> getDevicesNames() {
        return repository.getDevicesNames();
    }

    public Set<String> getMacropadsNames() {
        return repository.getMacropadsNames();
    }

    public SseEmitter createEmitter(String deviceId, DeviceType deviceType) {
        SseEmitter emitter = new SseEmitter(eventsTimeout);
        emitter.onCompletion(() -> {
            log.debug("COMPLETION");
            repository.removeEmitter(deviceId);
        });
        emitter.onTimeout(() -> {
            log.debug("TIMEOUT");
            repository.removeEmitter(deviceId);
        });
        emitter.onError(e -> {
            log.debug("Create SseEmitter exception", e);
            repository.removeEmitter(deviceId);
        });
        repository.addOrReplaceEmitter(deviceId, emitter, deviceType);
        return emitter;
    }

    public void unregisterAll() {
        repository.getDevicesNames()
                .forEach(repository::unregister);
        repository.getMacropadsNames()
                .forEach(repository::unregister);
    }
}
