package com.itninja.ssenotificationcore.repository;

import java.util.Optional;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {

    void addOrReplaceEmitter(String username, SseEmitter emitter);

    void remove(String username);

    Optional<SseEmitter> getEmitter(String username);
}
