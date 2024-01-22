package com.itninja.ssenotificationcore.service;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.repository.EmitterRepository;

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

    public SseEmitter createEmitter(String deviceId) {
        SseEmitter emitter = new SseEmitter(eventsTimeout);
        emitter.onCompletion(() -> {
            log.debug("COMPLETION");
            repository.remove(deviceId);
        });
        emitter.onTimeout(() -> {
            log.debug("TIMEOUT");
            repository.remove(deviceId);
        });
        emitter.onError(e -> {
            log.debug("Create SseEmitter exception", e);
            repository.remove(deviceId);
        });
        repository.addOrReplaceEmitter(deviceId, emitter);
        return emitter;
    }
}
