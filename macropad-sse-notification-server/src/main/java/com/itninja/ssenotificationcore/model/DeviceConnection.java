package com.itninja.ssenotificationcore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConnection {
    private SseEmitter emitter;
    private DeviceType deviceType;
}
