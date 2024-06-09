package com.alerts;

import java.util.List;
import com.data_management.PatientRecord;
import java.util.stream.Collectors;

public class HeartRateStrategy implements AlertStrategy {

    private ECGAlertFactory factory = new ECGAlertFactory();

    @Override
    public void checkAlert(List<PatientRecord> records, AlertGenerator alertGenerator) {
        List<PatientRecord> ECGRecords = records.stream()
                .filter(record -> "ECG".equals(record.getRecordType()))
                .collect(Collectors.toList());

        // Abnormal Heart Rate Alert
        for (PatientRecord record : ECGRecords) {
            double heartRate = record.getMeasurementValue();
            if (heartRate < 50 || heartRate > 100) {
                alertGenerator.triggerAlert(factory.createAlert(
                        Integer.toString(record.getPatientId()), "Abnormal Heart Rate", record.getTimestamp()
                ));
            }
        }

        // Irregular Beat Alert
        for (int i = 2; i < ECGRecords.size(); i++) {
            PatientRecord prevRecord = ECGRecords.get(i - 1);
            PatientRecord currentRecord = ECGRecords.get(i);

            double prevHeartRate = prevRecord.getMeasurementValue();
            double currentHeartRate = currentRecord.getMeasurementValue();

            if (Math.abs(currentHeartRate - prevHeartRate) > 10) {
                alertGenerator.triggerAlert(factory.createAlert(
                        Integer.toString(currentRecord.getPatientId()), "Irregular Beat", currentRecord.getTimestamp()
                ));
            }
        }
    }
}

