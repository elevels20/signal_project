package com.alerts;

public class GenericAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "General: " + condition, timestamp);
    }
}