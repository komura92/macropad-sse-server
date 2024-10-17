package com.itninja.macropad.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.itninja.macropad.backend.notification.model.SseNotificationDTO;
import com.itninja.macropad.backend.utils.MacropadApiClient;
import com.itninja.macropad.backend.utils.TestDevice;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SingleDeviceStoryTest {

    @LocalServerPort
    int serverPort;
    private static MacropadApiClient macropadApiClient;


    public static final SseNotificationDTO TEST_ACTION_EVENT = SseNotificationDTO.builder()
            .action("single-test-action")
            .build();
    public static final SseNotificationDTO TEST_TAB_EVENT = SseNotificationDTO.builder()
            .tabIdentifier("single-test-tab-event")
            .build();

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
    void canMacropadSubscribe() throws InterruptedException {
        var disposable = macropadApiClient.subscribeSilently(TestDevice.MACROPAD_1);

        waitForSubscription();
        var macropads = macropadApiClient.getMacropads();
        assertThat(macropads)
                .describedAs("Macropad registered")
                .contains(TestDevice.MACROPAD_1.getId());
        assertThat(macropads.size())
                .describedAs("One macropad registered")
                .isEqualTo(1);
        assertThat(macropadApiClient.getDevices())
                .describedAs("Device is not registered")
                .isEmpty();

        disposable.dispose();
    }

    @Test
    @Order(3)
    void canPcSubscribe() throws InterruptedException {
        var disposable = macropadApiClient.subscribeSilently(TestDevice.DEVICE_1);

        waitForSubscription();
        var devices = macropadApiClient.getDevices();

        assertThat(devices)
                .describedAs("Device registered")
                .contains(TestDevice.DEVICE_1.getId());
        assertThat(devices.size())
                .describedAs("One device registered")
                .isEqualTo(1);
        assertThat(macropadApiClient.getMacropads().size())
                .describedAs("No new macropads registered")
                .isEqualTo(1);

        disposable.dispose();
    }

    @Test
    @Order(4)
    void canActionBeSentToPc() throws InterruptedException {
        List<SseNotificationDTO> receivedEvents = new ArrayList<>();
        var disposable = macropadApiClient.subscribe(TestDevice.DEVICE_1, receivedEvents::add);

        waitForSubscription();

        macropadApiClient.sendNotification(TestDevice.DEVICE_1, TEST_ACTION_EVENT);

        Awaitility.await()
                .pollDelay(1, TimeUnit.SECONDS)
                .timeout(20, TimeUnit.SECONDS)
                .until(() -> receivedEvents.size() == 1);

        SseNotificationDTO receivedEvent = receivedEvents.get(0);

        assertThat(macropadApiClient.getDevices().size())
                .describedAs("No new devices registered")
                .isEqualTo(1);

        assertThat(receivedEvent.getAction())
                .describedAs("Event has proper action")
                .isEqualTo(TEST_ACTION_EVENT.getAction());

        disposable.dispose();
    }

    @Test
    @Order(5)
    void canTabChangeBeSentToMacropad() throws InterruptedException {
        List<SseNotificationDTO> receivedEvents = new ArrayList<>();
        var disposable = macropadApiClient.subscribe(TestDevice.MACROPAD_1, receivedEvents::add);
        waitForSubscription();

        macropadApiClient.sendNotification(TestDevice.MACROPAD_1, TEST_TAB_EVENT);

        Awaitility.await()
                .pollDelay(1, TimeUnit.SECONDS)
                .until(() -> receivedEvents.size() == 1);

        SseNotificationDTO receivedEvent = receivedEvents.get(0);

        assertThat(macropadApiClient.getMacropads().size())
                .describedAs("No new macropads registered")
                .isEqualTo(1);

        assertThat(receivedEvent.getTabIdentifier())
                .describedAs("Event has proper tab identifier")
                .isEqualTo(TEST_TAB_EVENT.getTabIdentifier());

        disposable.dispose();
    }

    @AfterAll
    static void cleanup() {
        macropadApiClient.unregisterAll();
    }

    private void waitForSubscription() throws InterruptedException {
        Thread.sleep(200);
    }
}
