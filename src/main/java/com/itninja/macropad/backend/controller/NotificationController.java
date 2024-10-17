package com.itninja.macropad.backend.controller;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itninja.macropad.backend.service.EmitterService;
import com.itninja.macropad.backend.service.SseNotificationService;
import com.itninja.macropad.backend.model.DeviceType;
import com.itninja.macropad.backend.notification.model.SseNotificationDTO;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(NotificationController.NOTIFICATIONS_CONTROLLER_PATH)
public class NotificationController {
    public static final String NOTIFICATIONS_CONTROLLER_PATH = "notifications";
    public static final String SUBSCRIBE_PATH = "subscribe";
    public static final String DEVICES_PATH = "devices";
    public static final String MACROPADS_PATH = "macropads";

    private final EmitterService emitterService;
    private final SseNotificationService notificationService;

    @GetMapping(SUBSCRIBE_PATH)
    public SseEmitter subscribeToEvents(@RequestParam String deviceId,
                                        @RequestParam DeviceType deviceType) {
        return emitterService.createEmitter(deviceId, deviceType);
    }

    @GetMapping(DEVICES_PATH)
    public Set<String> getDevicesNames() {
        return emitterService.getDevicesNames();
    }

    @GetMapping(MACROPADS_PATH)
    public Set<String> getMacropadsNames() {
        return emitterService.getMacropadsNames();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SseNotificationDTO publishEvent(@RequestParam("deviceId") List<String> deviceIds,
                                           @RequestBody SseNotificationDTO event) {
        notificationService.sendNotifications(deviceIds, event);
        return event;
    }


    @DeleteMapping
    public void unregisterAll() {
        emitterService.unregisterAll();
    }
}
