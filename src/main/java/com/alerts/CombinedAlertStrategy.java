package com.alerts;

import java.util.List;
import com.data_management.PatientRecord;
import java.util.stream.Collectors;

public class CombinedAlertStrategy implements AlertStrategy {

    private CombinedAlertFactory factory = new CombinedAlertFactory();

    @Override
    public void checkAlert(List<PatientRecord> records, AlertGenerator alertGenerator) {
        List<PatientRecord> systolicBloodPressureRecords = records.stream()
                .filter(record -> "Systolic Blood Pressure".equals(record.getRecordType()))
                .collect(Collectors.toList());

        List<PatientRecord> bloodSaturationRecords = records.stream()
                .filter(record -> "Blood Saturation".equals(record.getRecordType()))
                .collect(Collectors.toList());

        // Hypotensive Hypoxemia Alert
        for (PatientRecord systolicRecord : systolicBloodPressureRecords) {
            double systolicBloodPressure = systolicRecord.getMeasurementValue();
            long systolicTime = systolicRecord.getTimestamp();
            if (systolicBloodPressure < 90) {
                for (PatientRecord saturationRecord : bloodSaturationRecords) {
                    long saturationTime = saturationRecord.getTimestamp();
                    if (systolicTime == saturationTime) {
                        double saturationValue = saturationRecord.getMeasurementValue();
                        if (saturationValue < 92) {
                            alertGenerator.triggerAlert(factory.createAlert(
                                    Integer.toString(systolicRecord.getPatientId()),
                                    "Hypotensive Hypoxemia", saturationTime
                            ));
                        }
                    }
                }
            }
        }
    }
}
