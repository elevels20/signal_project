package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;

public class DataReaderImplementation implements DataReader{

    private DataStorage dataStorage;
    private String outDirectory;
    private WebSocketClient webSocketClient;

    public DataReaderImplementation (DataStorage dataStorage, String directory){
        this.dataStorage = dataStorage;
        this.outDirectory = directory;
    }

    @Override
    public void connectToWebSocket(DataStorage dataStorage, String uri) throws IOException {
        this.dataStorage = dataStorage;
        try {
            webSocketClient = new WebSocketClientImplementation(dataStorage, new URI(uri)); 
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI: " + uri, e);
        }
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File directory = new File(outDirectory);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Invalid directory: " + directory);
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    readFromFile(file);
                }
            }
        }
    }

    private void readFromFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse data from each line
                // Assuming format: patientId,measurementValue,recordType,timestamp
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int patientId = Integer.parseInt(parts[0]);
                    double measurementValue = Double.parseDouble(parts[1]);
                    String recordType = parts[2];
                    long timestamp = Long.parseLong(parts[3]);

                    // Store data using DataStorage
                    dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                } else {
                    System.err.println("Invalid data format: " + line);
                }
            }
        }
    }
}
