package com.itninja.macropad.backend.mapper;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.itninja.macropad.backend.notification.model.SseNotificationDTO;


@Component
@AllArgsConstructor
public class EventMapper {

    private static final ObjectWriter mapper = new ObjectMapper().writer();

    @SneakyThrows
    public SseEmitter.SseEventBuilder toSseEventBuilder(SseNotificationDTO event) {
        return SseEmitter.event()
                .id(RandomStringUtils.randomAlphanumeric(12))
                .name(event.getAction())
                .data(mapper.writeValueAsString(event));
    }
}
