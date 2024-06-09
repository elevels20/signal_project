package com.alerts;

import java.util.List;
import com.data_management.PatientRecord;
import java.util.stream.Collectors;

public class OxygenSaturationStrategy implements AlertStrategy {

    private BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

    @Override
    public void checkAlert(List<PatientRecord> records, AlertGenerator alertGenerator) {
        List<PatientRecord> bloodSaturationRecords = records.stream()
                .filter(record -> "Blood Saturation".equals(record.getRecordType()))
                .collect(Collectors.toList());

        // Low Saturation Alert
        for (PatientRecord record : bloodSaturationRecords) {
            double value = record.getMeasurementValue();
            if (value < 92) {
                alertGenerator.triggerAlert(factory.createAlert(
                        Integer.toString(record.getPatientId()), "Low blood oxygen saturation", record.getTimestamp()
                ));
            }
        }

        // Rapid Drop Alert
        for (int i = 0; i < bloodSaturationRecords.size(); i++) {
            PatientRecord startRecord = bloodSaturationRecords.get(i);

            long startTime = startRecord.getTimestamp();
            double startValue = startRecord.getMeasurementValue();

            for (int j = i + 1; j < bloodSaturationRecords.size(); j++) {
                PatientRecord endRecord = bloodSaturationRecords.get(j);

                long endTime = endRecord.getTimestamp();
                double endValue = endRecord.getMeasurementValue();

                if ((endTime - startTime <= (10 * 60 * 1000)) && (startValue - endValue >= 5)) {
                    alertGenerator.triggerAlert(factory.createAlert(
                            Integer.toString(endRecord.getPatientId()), "Rapid Drop in Blood Saturation", endRecord.getTimestamp()
                    ));
                }
            }
        }
    }
}
