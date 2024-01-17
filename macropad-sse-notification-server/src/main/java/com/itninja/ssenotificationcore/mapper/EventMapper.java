package com.itninja.ssenotificationcore.mapper;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.notification.model.SseNotificationDTO;


@Component
@AllArgsConstructor
public class EventMapper {

    public SseEmitter.SseEventBuilder toSseEventBuilder(SseNotificationDTO event) {
        return SseEmitter.event()
                .id(RandomStringUtils.randomAlphanumeric(12))
                .name(event.getAction())
                .data("{\"action\":\"" + event.getAction() + "\"}");
    }
}
