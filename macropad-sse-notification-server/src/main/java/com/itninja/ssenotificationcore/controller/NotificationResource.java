package com.itninja.ssenotificationcore.controller;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.ssenotificationcore.notification.model.SseNotificationDTO;
import com.itninja.ssenotificationcore.service.EmitterService;
import com.itninja.ssenotificationcore.service.SseNotificationService;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(NotificationResource.NOTIFICATIONS_CONTROLLER_PATH)
@RequiredArgsConstructor
public class NotificationResource {
    public static final String NOTIFICATIONS_CONTROLLER_PATH = "notifications";
    public static final String SUBSCRIBE_PATH = "subscribe";
    public static final String DEVICES_PATH = "devices";

    private final EmitterService emitterService;
    private final SseNotificationService notificationService;

    @GetMapping(SUBSCRIBE_PATH)
    public SseEmitter subscribeToEvents(@RequestParam String deviceId) {
        return emitterService.createEmitter(deviceId);
    }

    @GetMapping(DEVICES_PATH)
    public Set<String> getDevicesNames() {
        return emitterService.getDevicesNames();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SseNotificationDTO publishEvent(@RequestParam("deviceId") List<String> deviceIds,
                                           @RequestBody SseNotificationDTO event) {
        notificationService.sendNotifications(deviceIds, event);
        return event;
    }
}
