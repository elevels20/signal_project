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
    private List<AlertStrategy> alertStrategies;

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
        this.alertStrategies = new ArrayList<>();
        // Add default alert handlers
        this.alertHandlers.add(new FileAlertHandler("alerts.log"));
        this.alertHandlers.add(new PagerAlertHandler(new Pager()));
        // Add default alert strategies
        this.alertStrategies.add(new BloodPressureStrategy());
        this.alertStrategies.add(new HeartRateStrategy());
        this.alertStrategies.add(new OxygenSaturationStrategy());
        this.alertStrategies.add(new CombinedAlertStrategy());
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
        // Check for each type of alert condition using strategies
        for (AlertStrategy strategy : alertStrategies) {
            strategy.checkAlert(patientRecords, this);
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
    public void triggerAlert(Alert alert) {
        for (AlertHandler handler : alertHandlers) {
            handler.handleAlert(alert);
        }
    }
}