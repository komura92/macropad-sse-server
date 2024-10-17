package com.itninja.macropad.backend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import reactor.core.Disposable;

import com.itninja.macropad.backend.model.DeviceType;
import com.itninja.macropad.backend.notification.model.SseNotificationDTO;
import com.itninja.macropad.backend.utils.MacropadApiClient;
import com.itninja.macropad.backend.utils.TestDevice;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MultipleDevicesStoryTest {

    public static final SseNotificationDTO TEST_ACTION_EVENT = SseNotificationDTO.builder()
            .action("multiple-test-action")
            .build();
    public static final SseNotificationDTO TEST_TAB_EVENT = SseNotificationDTO.builder()
            .tabIdentifier("multiple-test-tab")
            .build();


    List<TestDevice> devicesToControl = List.of(TestDevice.DEVICE_1, TestDevice.DEVICE_2);
    List<TestDevice> macropadsToNotify = List.of(TestDevice.MACROPAD_1, TestDevice.MACROPAD_2);

    static Map<TestDevice, List<SseNotificationDTO>> notifications = new HashMap<>();
    static Map<TestDevice, Disposable> disposables = new HashMap<>();


    @LocalServerPort
    int serverPort;

    static MacropadApiClient macropadApiClient;


    @BeforeEach
    void setClientPort() {
        macropadApiClient = new MacropadApiClient(serverPort);
    }


    @Test
    @Order(1)
    void arePrerequisitesFilled() {
        var devices = macropadApiClient.getDevices();
        assertThat(devices)
                .describedAs("Initial devices list is empty")
                .isEmpty();

        var macropads = macropadApiClient.getMacropads();
        assertThat(macropads)
                .describedAs("Initial macropads list is empty")
                .isEmpty();
    }

    @Test
    @Order(2)
    void canUserRegisterAllSixDevices() throws InterruptedException {
        for (TestDevice device : TestDevice.values())
            disposables.put(device, macropadApiClient.subscribe(device,
                    event -> {
                        if (!notifications.containsKey(device))
                            notifications.put(device, Lists.newArrayList());
                        notifications.get(device).add(event);
                    }));

        waitForSubscription();

        var devices = macropadApiClient.getDevices();

        assertThat(devices)
                .describedAs("All devices successfully registered")
                .isEqualTo(TestDevice.getAllByType(DeviceType.PC).stream().map(TestDevice::getId).collect(Collectors.toSet()));

        var macropads = macropadApiClient.getMacropads();

        assertThat(macropads)
                .describedAs("All macropads successfully registered")
                .containsAll(TestDevice.getAllByType(DeviceType.MACROPAD).stream().map(TestDevice::getId).collect(Collectors.toSet()));
    }

    @Test
    @Order(3)
    void canActionBeSentToSpecificDevices() {
        macropadApiClient.sendNotification(devicesToControl, TEST_ACTION_EVENT);

        Awaitility.await()
                .pollDelay(1, TimeUnit.SECONDS)
                .timeout(20, TimeUnit.SECONDS)
                .until(() -> devicesToControl.stream()
                        .allMatch(device -> CollectionUtils.isNotEmpty(notifications.get(device))));

        devicesToControl.forEach(device -> assertThat(notifications.get(device).get(0).getAction())
                .describedAs("Proper notification received")
                .isEqualTo(TEST_ACTION_EVENT.getAction()));

        Arrays.stream(TestDevice.values())
                .filter(device -> !devicesToControl.contains(device))
                .forEach(device -> assertThat(notifications.getOrDefault(device, Lists.newArrayList()))
                        .describedAs("Notification send to proper devices only")
                        .isEmpty());

        notifications.clear();
    }

    @Test
    @Order(4)
    void canTabChangeBeSentToSpecificMacropads() {
        macropadApiClient.sendNotification(macropadsToNotify, TEST_TAB_EVENT);

        Awaitility.await()
                .pollDelay(1, TimeUnit.SECONDS)
                .timeout(20, TimeUnit.SECONDS)
                .until(() -> macropadsToNotify.stream()
                        .allMatch(macropad -> CollectionUtils.isNotEmpty(notifications.get(macropad))));

        macropadsToNotify.forEach(macropad -> assertThat(notifications.get(macropad).get(0).getTabIdentifier())
                .describedAs("Proper notification received")
                .isEqualTo(TEST_TAB_EVENT.getTabIdentifier()));

        Arrays.stream(TestDevice.values())
                .filter(device -> !macropadsToNotify.contains(device))
                .forEach(device -> assertThat(notifications.getOrDefault(device, Lists.newArrayList()))
                        .describedAs("Notification send to proper devices only")
                        .isEmpty());
    }

    @AfterAll
    static void cleanup() {
        disposables.values()
                .forEach(Disposable::dispose);

        macropadApiClient.unregisterAll();
    }

    private void waitForSubscription() throws InterruptedException {
        Thread.sleep(200);
    }
}
