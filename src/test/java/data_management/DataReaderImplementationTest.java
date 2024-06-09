package data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.data_management.DataStorage;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.DataReader;
import com.data_management.DataReaderImplementation;
import com.data_management.Patient;
import com.data_management.WebSocketClientImplementation;

import static org.junit.jupiter.api.Assertions.*;

public class DataReaderImplementationTest {

    private DataStorage dataStorage;
    private DataReader dataReader;
    private int port;
    private WebSocketOutputStrategy outputStrategy;

    @BeforeEach
    public void setUp() {
        dataStorage = DataStorage.getInstance();
        dataReader = new DataReaderImplementation(dataStorage, "test_directory");

        // Start the WebSocket server
        port = 8080;
        outputStrategy = new WebSocketOutputStrategy(port);
    }

    @AfterEach
    void tearDown() {
        if (outputStrategy != null) {
            outputStrategy.stopServer();
        }
    }

    // @Test
    // public void testConnectToWebSocket_SuccessfulConnection() throws IOException {
    //         // Create the URI for the WebSocket server
    //         URI serverUri;
    //         try {
    //             serverUri = new URI("ws://localhost:" + port);
    //         } catch (URISyntaxException e) {
    //             e.printStackTrace();
    //             return;
    //         }

    //         // Call the method being tested
    //         dataReader.connectToWebSocket(dataStorage, serverUri.toString());
    //         WebSocketClientImplementation webSocketClient = dataReader.getWebSocket();

    //         // Add a delay to ensure connection setup before sending messages and checking connection
    //         try {
    //             Thread.sleep(1000); // Adjust the delay as needed
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         }
    //         // Verify that the WebSocketClient's connect method was called
    //         assertTrue(webSocketClient.isConnected());

    //         if (webSocketClient != null && webSocketClient.isOpen()) {
    //             webSocketClient.close();
    //         }
    // }
    @Test
    public void testConnectToWebSocket_SuccessfulConnection() throws IOException, URISyntaxException, InterruptedException {
        // Create the URI for the WebSocket server
        URI serverUri;
        try {
            serverUri = new URI("ws://localhost:" + port);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        // Call the method being tested
        dataReader.connectToWebSocket(dataStorage, serverUri.toString());
        WebSocketClientImplementation webSocketClient = dataReader.getWebSocket();

        // Use CountDownLatch for synchronization
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
        assertTrue(webSocketClient.isConnected());

        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
    }

    @Test
    public void testConnectToWebSocket_InvalidURI() {
        String invalidUri = "invalid_uri";
        assertThrows(IOException.class, () -> {
            dataReader.connectToWebSocket(dataStorage, invalidUri);
        });
    }


    @Test
    public void testReadData_ValidFiles() throws IOException {
        // Create test directory and files
        File directory = new File("test_directory");
        directory.mkdir();
        File file1 = new File(directory, "test1.txt");
        File file2 = new File(directory, "test2.txt");
        file1.createNewFile();
        file2.createNewFile();

        // Write test data to files
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("1,70.5,HeartRate,1623061845000\n");
            writer.write("1,120,HeartRate,1623061855000\n");
        }
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("2,120,BloodPressure,1623061845000\n");
            writer.write("2,80,BloodPressure,1623061855000\n");
        }

        // Read data
        dataReader.readData(dataStorage);

        long startTime = 0L; // UNIX epoch start time
        long endTime = System.currentTimeMillis(); // Current time
        // Verify data is stored correctly
        List<Patient> patients = dataStorage.getAllPatients();
        assertEquals(2, patients.size());

        Patient patient1 = patients.get(0);
        assertEquals(1, patient1.getPatientId());
        assertEquals(2, patient1.getRecords(startTime, endTime).size());

        Patient patient2 = patients.get(1);
        assertEquals(2, patient2.getPatientId());
        assertEquals(2, patient2.getRecords(startTime, endTime).size());

        // Clean up
        file1.delete();
        file2.delete();
        directory.delete();
    }

    @Test
    public void testReadData_InvalidDirectory() {
        try {
            dataReader.readData(dataStorage);
            // Expecting IOException to be thrown due to invalid directory
            fail("Expected IOException");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Invalid directory"));
        }
    }
}
