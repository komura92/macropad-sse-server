package com.itninja.macropad.backend.utils;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.itninja.macropad.backend.model.DeviceType;

@Getter
@AllArgsConstructor
public enum TestDevice {
    MACROPAD_1("macropad-id-1", DeviceType.MACROPAD),
    MACROPAD_2("macropad-id-2", DeviceType.MACROPAD),
    MACROPAD_3("macropad-id-3", DeviceType.MACROPAD),
    DEVICE_1("device-id-1", DeviceType.PC),
    DEVICE_2("device-id-2", DeviceType.PC),
    DEVICE_3("device-id-3", DeviceType.PC);

    private final String id;
    private final DeviceType deviceType;

    public static List<TestDevice> getAllByType(DeviceType deviceType) {
        return Arrays.stream(values())
                .filter(device -> deviceType.equals(device.getDeviceType()))
                .toList();
    }
}
