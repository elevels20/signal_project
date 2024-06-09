package com.alerts;

public class CombinedAlertFactory extends AlertFactory{
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "Blood pressure + Blood saturation: " + condition, timestamp);
    }
}
