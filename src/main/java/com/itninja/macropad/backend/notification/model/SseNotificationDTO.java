package com.itninja.macropad.backend.notification.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SseNotificationDTO implements Serializable {
    private String action;
    private String tabIdentifier;
}
