package com.itninja.ssenotificationcore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.repository.InMemoryEmitterRepository;

@Service
@Slf4j
public class EmitterService {

    private final long eventsTimeout;
    private final InMemoryEmitterRepository repository;

    public EmitterService(@Value("${events.connection.timeout}") long eventsTimeout,
                          InMemoryEmitterRepository repository) {
        this.eventsTimeout = eventsTimeout;
        this.repository = repository;
    }

    public SseEmitter createEmitter(String deviceId) {
        SseEmitter emitter = new SseEmitter(eventsTimeout);
        emitter.onCompletion(() -> {
            log.error("COMPLETION");
            repository.remove(deviceId);
        });
        emitter.onTimeout(() -> {
            log.error("TIMEOUT");
            repository.remove(deviceId);
        });
        emitter.onError(e -> {
            log.error("Create SseEmitter exception", e);
            repository.remove(deviceId);
        });
        repository.addOrReplaceEmitter(deviceId, emitter);
        return emitter;
    }
}
