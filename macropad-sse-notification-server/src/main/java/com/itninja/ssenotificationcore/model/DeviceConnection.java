package com.itninja.ssenotificationcore.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.notification.model.SseNotificationDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConnection {
    private SseEmitter emitter;

    @Builder.Default
    List<SseNotificationDTO> notificationsToSend = new ArrayList<>();
}
