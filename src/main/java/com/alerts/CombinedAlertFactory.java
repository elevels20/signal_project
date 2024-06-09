package com.alerts;

public class CombinedAlertFactory extends AlertFactory{
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "Systolic blood pressure + blood saturation: " + condition, timestamp);
    }
}
