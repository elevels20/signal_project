package com.alerts;

import java.util.List;
import com.data_management.PatientRecord;
import java.util.stream.Collectors;

public class BloodPressureStrategy implements AlertStrategy {

    private BloodPressureAlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public void checkAlert(List<PatientRecord> records, AlertGenerator alertGenerator) {
        List<PatientRecord> systolicBloodPressureRecords = records.stream()
                .filter(record -> "Systolic Blood Pressure".equals(record.getRecordType()))
                .collect(Collectors.toList());

        List<PatientRecord> diastolicBloodPressureRecords = records.stream()
                .filter(record -> "Diastolic Blood Pressure".equals(record.getRecordType()))
                .collect(Collectors.toList());

        // Trend Alert systolic blood pressure
        if (systolicBloodPressureRecords.size() > 2) {
            for (int i = 2; i < systolicBloodPressureRecords.size(); i++) {
                double prevSystolic = systolicBloodPressureRecords.get(i - 2).getMeasurementValue();
                double currSystolic = systolicBloodPressureRecords.get(i - 1).getMeasurementValue();
                double nextSystolic = systolicBloodPressureRecords.get(i).getMeasurementValue();

                if (Math.abs(currSystolic - prevSystolic) > 10 && Math.abs(nextSystolic - currSystolic) > 10) {
                    alertGenerator.triggerAlert(factory.createAlert(
                            Integer.toString(systolicBloodPressureRecords.get(i).getPatientId()),
                            "Systolic Trend Alert", systolicBloodPressureRecords.get(i).getTimestamp()
                    ));
                }
            }
        }

        // Trend Alert diastolic blood pressure
        if (diastolicBloodPressureRecords.size() > 2) {
            for (int i = 2; i < diastolicBloodPressureRecords.size(); i++) {
                double prevDiastolic = diastolicBloodPressureRecords.get(i - 2).getMeasurementValue();
                double currDiastolic = diastolicBloodPressureRecords.get(i - 1).getMeasurementValue();
                double nextDiastolic = diastolicBloodPressureRecords.get(i).getMeasurementValue();

                if (Math.abs(currDiastolic - prevDiastolic) > 10 && Math.abs(nextDiastolic - currDiastolic) > 10) {
                    alertGenerator.triggerAlert(factory.createAlert(
                            Integer.toString(systolicBloodPressureRecords.get(i).getPatientId()),
                            "Diastolic Trend Alert", systolicBloodPressureRecords.get(i).getTimestamp()
                    ));
                }
            }
        }

        // Critical Threshold Alert
        for (PatientRecord record : systolicBloodPressureRecords) {
            double value = record.getMeasurementValue();
            if (value > 180 || value < 90) {
                alertGenerator.triggerAlert(factory.createAlert(
                        Integer.toString(record.getPatientId()), "Critical Systolic Blood Pressure",
                        record.getTimestamp()
                ));
            }
        }

        for (PatientRecord record : diastolicBloodPressureRecords) {
            double value = record.getMeasurementValue();
            if (value > 120 || value < 60) {
                alertGenerator.triggerAlert(factory.createAlert(
                        Integer.toString(record.getPatientId()), "Critical Diastolic Blood Pressure",
                        record.getTimestamp()
                ));
            }
        }
    }
}
