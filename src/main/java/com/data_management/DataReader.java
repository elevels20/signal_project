package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a WebSocket server and continuously receive data
     * 
     * @param dataStorage the storage where data will be stored
     * @param uri the URI of the WebSocket server
     * @throws IOException if there is an error connecting to the WebSocket server or reading the data
     */
    void connectToWebSocket(DataStorage dataStorage, String uri) throws IOException;
}
