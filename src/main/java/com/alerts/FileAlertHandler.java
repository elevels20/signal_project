package com.alerts;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileAlertHandler implements AlertHandler{
    private String filePath;

    public FileAlertHandler(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void handleAlert(Alert alert) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println("Alert triggered:");
            writer.println("Patient ID: " + alert.getPatientId());
            writer.println("Alert Condition: " + alert.getCondition());
            writer.println("Timestamp: " + alert.getTimestamp());
            writer.println("------------------------------------");
        } catch (IOException e) {
            System.err.println("Error writing alert to file: " + e.getMessage());
        }
    }
}
