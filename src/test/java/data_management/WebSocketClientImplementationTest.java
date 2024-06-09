package data_management;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.WebSocketClientImplementation;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.DataStorage;

class WebSocketClientImplementationTest {

    private DataStorage dataStorage;
    private WebSocketClientImplementation webSocketClient;
    private int port;
    private WebSocketOutputStrategy outputStrategy;

    @BeforeEach
    void setUp() throws URISyntaxException {
        dataStorage = DataStorage.getInstance();
        // Start the WebSocket server
        port = 8080;
        outputStrategy = new WebSocketOutputStrategy(port);

        // Create the URI for the WebSocket server
        URI serverUri;
        try {
            serverUri = new URI("ws://localhost:" + port);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        // Create and connect the WebSocket client
        webSocketClient = new WebSocketClientImplementation(dataStorage, serverUri);
        webSocketClient.connect();
    }

    @AfterEach
    void tearDown() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
        if (outputStrategy != null) {
            outputStrategy.stopServer();
        }
    }

    @Test
    void testOnOpen() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            while (!webSocketClient.isOpen()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            latch.countDown();
        }).start();

        // Wait up to 5 seconds for the connection to be established
        boolean connected = latch.await(5, TimeUnit.SECONDS);
        assertTrue(connected, "WebSocketClient should be connected within the timeout period.");
        assertTrue(webSocketClient.isOpen());
    }

    @Test
    void testOnMessage_ValidMessage() {
        webSocketClient.onMessage("1,98.6,HeartRate,1609459200000");
        long startTime = 0L; // UNIX epoch start time
        long endTime = System.currentTimeMillis(); // Current time
        assertEquals(1, dataStorage.getAllPatients().get(0).getPatientId());
        assertEquals(98.6, dataStorage.getAllPatients().get(0).getRecords(startTime, endTime).get(0).getMeasurementValue());
        assertEquals("HeartRate", dataStorage.getAllPatients().get(0).getRecords(startTime, endTime).get(0).getRecordType());
        assertEquals(1609459200000L, dataStorage.getAllPatients().get(0).getRecords(startTime,endTime).get(0).getTimestamp());
    }

    @Test
    void testOnMessage_InvalidMessage() {
        webSocketClient.onMessage("1,98.6,HeartRate"); // Missing timestamp
        assertTrue(dataStorage.getAllPatients().isEmpty());
    }

    @Test
    void testOnClose() {
        webSocketClient.onClose(1000, "Normal closure", true);
        assertFalse(webSocketClient.isOpen());
    }

    @Test
    void testOnError() {
        webSocketClient.onError(new Exception("Test exception"));
        assertFalse(webSocketClient.isOpen());
    }
}
