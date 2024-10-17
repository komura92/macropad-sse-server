package com.itninja.macropad.backend.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import com.itninja.macropad.backend.notification.model.SseNotificationDTO;

public class MacropadApiClient {

    private final WebClient webClient;

    private final ParameterizedTypeReference<ServerSentEvent<Map<String, Object>>> type =
            new ParameterizedTypeReference<>() {
            };

    public MacropadApiClient(int serverPort) {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:" + serverPort)
                .build();
    }

    public Disposable subscribeSilently(TestDevice device) {
        return this.subscribe(device, event -> {
        });
    }

    public Flux<ServerSentEvent<Map<String, Object>>> subscribe(TestDevice device) {
        var requestSpec = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sse/notifications/subscribe")
                        .queryParam("deviceId", device.getId())
                        .queryParam("deviceType", device.getDeviceType())
                        .build());

        return requestSpec.retrieve().bodyToFlux(type);
    }

    public Disposable subscribe(TestDevice device,
                                Consumer<SseNotificationDTO> onEventReceivedCallback) {
        return subscribe(device).subscribe(
                event -> {
                    SseNotificationDTO receivedNotification = new SseNotificationDTO(event.event(),
                            Optional.ofNullable(event.data().get("tabIdentifier")).map(Objects::toString).orElse(null));
                    onEventReceivedCallback.accept(receivedNotification);
                },
                error -> {
                    throw new AssertionError("Event stream error", error);
                });
    }

    public Set<String> getMacropads() {
        return webClient.get()
                .uri("/sse/notifications/macropads")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {
                })
                .block();
    }

    public Set<String> getDevices() {
        return webClient.get()
                .uri("/sse/notifications/devices")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {
                })
                .block();
    }

    public void unregisterAll() {
        webClient.delete()
                .uri("/sse/notifications")
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void sendNotification(List<TestDevice> targetDevices, SseNotificationDTO notification) {
        var targetDevicesIds = targetDevices.stream()
                .map(TestDevice::getId)
                .toList();
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/sse/notifications")
                        .queryParam("deviceId", targetDevicesIds)
                        .build())
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(SseNotificationDTO.class)
                .block();
    }

    public void sendNotification(TestDevice device, SseNotificationDTO notification) {
        this.sendNotification(List.of(device), notification);
    }
}
