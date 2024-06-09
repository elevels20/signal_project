package com.alerts;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<AlertHandler> alertHandlers;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertHandlers = new ArrayList<>();
        // Add default alert handlers
        this.alertHandlers.add(new FileAlertHandler("alerts.log"));
        this.alertHandlers.add(new PagerAlertHandler(new Pager()));
    }

    public List<AlertHandler> getAlertHandlers() {
        return alertHandlers;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> patientRecords = dataStorage.getRecords(patient.getPatientId(), Long.MIN_VALUE, Long.MAX_VALUE);

        List<PatientRecord> systolicBloodPressureRecords = new ArrayList<PatientRecord>();
        List<PatientRecord> diastolicBloodPressureRecords = new ArrayList<PatientRecord>();
        List<PatientRecord> bloodSaturationRecords = new ArrayList<PatientRecord>();
        List<PatientRecord> ECGRecords = new ArrayList<PatientRecord>();

        for (int i = 0; i < patientRecords.size(); i++) {
            PatientRecord record = patientRecords.get(i);
            String type = record.getRecordType();
            if (type.equals("Systolic Blood Pressure")) {
                systolicBloodPressureRecords.add(record);
            }
            else if (type.equals("Diastolic Blood Pressure")) {
                diastolicBloodPressureRecords.add(record);
            }
            else if (type.equals("Blood Saturation")) {
                bloodSaturationRecords.add(record);
            }
            else if (type.equals("ECG")) {
                ECGRecords.add(record);
            }
            else { // Triggered Alert (by nurses or patients)
                triggerAlert(new GenericAlertFactory().createAlert(Integer.toString(record.getPatientId()), type, record.getTimestamp()));
            }
        }

        // Check for each type of alert condition
        checkBloodPressureAlerts(systolicBloodPressureRecords, diastolicBloodPressureRecords);
        checkBloodSaturationAlerts(bloodSaturationRecords);
        checkCombinedAlerts(systolicBloodPressureRecords, bloodSaturationRecords);
        checkECGAlerts(ECGRecords);

    }
    private void checkBloodPressureAlerts(List<PatientRecord> systolicBloodPressureRecords, List<PatientRecord> diastolicBloodPressureRecords) {
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        // Trend Alert systolic blood pressure
        if (systolicBloodPressureRecords.size() > 2) { // we need at least 3 consecutive readings for checking for trend alerts
            for (int i = 2; i < systolicBloodPressureRecords.size(); i++) {
                double prevSystolic = systolicBloodPressureRecords.get(i - 2).getMeasurementValue();
                double currSystolic = systolicBloodPressureRecords.get(i - 1).getMeasurementValue();
                double nextSystolic = systolicBloodPressureRecords.get(i).getMeasurementValue();

                if (Math.abs(currSystolic - prevSystolic) > 10 && Math.abs(nextSystolic - currSystolic) > 10) {
                    triggerAlert(factory.createAlert(Integer.toString(systolicBloodPressureRecords.get(i).getPatientId()), "Systolic Trend Alert", systolicBloodPressureRecords.get(i).getTimestamp()));
                }
            }
        }

        // Trend Alert diastolic blood pressure
        if (diastolicBloodPressureRecords.size() > 2) { // we need at least 3 consecutive readings for checking for trend alerts
            for (int i = 2; i < diastolicBloodPressureRecords.size(); i++) {
                double prevDiastolic = diastolicBloodPressureRecords.get(i - 2).getMeasurementValue();
                double currDiastolic = diastolicBloodPressureRecords.get(i - 1).getMeasurementValue();
                double nextDiastolic = diastolicBloodPressureRecords.get(i).getMeasurementValue();

                if (Math.abs(currDiastolic - prevDiastolic) > 10 && Math.abs(nextDiastolic - currDiastolic) > 10) {
                    triggerAlert(factory.createAlert(Integer.toString(systolicBloodPressureRecords.get(i).getPatientId()), "Diastolic Trend Alert", systolicBloodPressureRecords.get(i).getTimestamp()));
                }
            }
        }

        // Critical Threshold Alert
        for (PatientRecord record : systolicBloodPressureRecords) {
            double value = record.getMeasurementValue();
            if (value > 180 || value < 90) {
                triggerAlert(factory.createAlert(Integer.toString(record.getPatientId()), "Critical Systolic Blood Pressure", record.getTimestamp()));
            }
        }
        for (PatientRecord record : diastolicBloodPressureRecords) {
            double value = record.getMeasurementValue();
            if (value > 120 || value < 60) {
                triggerAlert(factory.createAlert(Integer.toString(record.getPatientId()), "Critical Diastolic Blood Pressure", record.getTimestamp()));
            }
        }
    }

    private void checkBloodSaturationAlerts(List<PatientRecord> bloodSaturationRecords) {
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();
        // Low Saturation Alert
        for (PatientRecord record : bloodSaturationRecords) {
            double value = record.getMeasurementValue();
            if (value < 92) {
                triggerAlert(factory.createAlert(Integer.toString(record.getPatientId()), "Low blood oxygen saturation", record.getTimestamp()));
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
                    // Blood oxygen saturation level drops by 5% or more within a 10-minute interval
                    triggerAlert(factory.createAlert(Integer.toString(endRecord.getPatientId()), "Rapid Drop in Blood Saturation", endRecord.getTimestamp()));
                }
            }
        }
    }

    private void checkCombinedAlerts(List<PatientRecord> systolicBloodPressureRecords, List<PatientRecord> bloodSaturationRecords) {
        CombinedAlertFactory factory = new CombinedAlertFactory();
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
                            triggerAlert(factory.createAlert(Integer.toString(systolicRecord.getPatientId()), "Hypotensive Hypoxemia", saturationTime));
                        }
                    }
                }
            }
        }
    }

    private void checkECGAlerts(List<PatientRecord> ECGRecords) {
        ECGAlertFactory factory = new ECGAlertFactory();
        // Abnormal Heart Rate Alert
        for (PatientRecord record : ECGRecords) {
            double heartRate = record.getMeasurementValue();
            if (heartRate < 50 || heartRate > 100) {
                triggerAlert(factory.createAlert(Integer.toString(record.getPatientId()), "Abnormal Heart Rate", record.getTimestamp()));
            }
        }
        // Irregular Beat Alert
        // Trigger alert if the difference in heart rate between consecutive records is more than 10 bpm
        //for (int i = 2; i < ECGRecords.size(); i++) {
        for (int i = 1; i < ECGRecords.size(); i++) {
            PatientRecord prevRecord = ECGRecords.get(i - 1);
            PatientRecord currentRecord = ECGRecords.get(i);

            double prevHeartRate = prevRecord.getMeasurementValue();
            double currentHeartRate = currentRecord.getMeasurementValue();

            if (Math.abs(currentHeartRate - prevHeartRate) > 10) {
                triggerAlert(factory.createAlert(Integer.toString(currentRecord.getPatientId()), "Irregular Beat", currentRecord.getTimestamp()));
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        for (AlertHandler handler : alertHandlers) {
            handler.handleAlert(alert);
        }
    }
}