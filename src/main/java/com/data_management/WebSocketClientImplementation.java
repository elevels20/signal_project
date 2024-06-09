package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.cardio_generator.outputs.WebSocketOutputStrategy;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClientImplementation extends WebSocketClient {

    private DataStorage dataStorage;
    private boolean isConnected = false;

    public WebSocketClientImplementation(DataStorage dataStorage, URI serverUri) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        isConnected = true;
        System.out.println("Connected to WebSocket server at " + getURI());
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void onMessage(String message) {
        // Assuming format: patientId,measurementValue,recordType,timestamp
        String[] parts = message.split(",");
        if (parts.length == 4) {
            try {
                int patientId = Integer.parseInt(parts[0]);
                double measurementValue = Double.parseDouble(parts[1]);
                String recordType = parts[2];
                long timestamp = Long.parseLong(parts[3]);

                // Store data using DataStorage
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            } catch (NumberFormatException e) {
                System.err.println("Invalid data format: " + message);
            }
        } else {
            System.err.println("Invalid data format: " + message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server: " + reason);
        // Raise an error for interruption in the data stream
        isConnected = false;
        onError(new Exception("Data stream interrupted"));
    }

    @Override
    public void onError(Exception ex) {
        //ex.printStackTrace();
        // Handle various exceptions that may occur during WebSocket communication
        if (ex instanceof URISyntaxException) {
            System.err.println("Invalid WebSocket URI: " + ex.getMessage());
        } else {
            System.err.println("WebSocket error: " + ex.getMessage());
        }
    }
    public static void main(String[] args) {
        // Create an instance of DataStorage 
        DataStorage dataStorage = DataStorage.getInstance();

        // Start the WebSocket server
        int port = 8080;
        WebSocketOutputStrategy outputStrategy = new WebSocketOutputStrategy(port);

        // Create the URI for the WebSocket server
        URI serverUri;
        try {
            serverUri = new URI("ws://localhost:" + port);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        // Create and connect the WebSocket client
        WebSocketClientImplementation client = new WebSocketClientImplementation(dataStorage, serverUri);
        client.connect();

        // Add a delay to ensure connection setup before sending messages
        try {
            Thread.sleep(1000); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now the client is connected to the server and can receive messages
    }
}
