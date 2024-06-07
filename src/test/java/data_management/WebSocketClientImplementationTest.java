package data_management;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.WebSocketClientImplementation;
import com.data_management.DataStorage;

class WebSocketClientImplementationTest {

    private DataStorage dataStorage;
    private WebSocketClientImplementation webSocketClient;

    @BeforeEach
    void setUp() throws URISyntaxException {
        dataStorage = new DataStorage();
        webSocketClient = new WebSocketClientImplementation(dataStorage, new URI("ws://localhost:8080/socket"));
    }

    @Test
    void testOnOpen() {
        webSocketClient.onOpen(null);
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
