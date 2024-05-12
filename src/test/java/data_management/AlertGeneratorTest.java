package data_management;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.alerts.AlertGenerator;
import com.data_management.Patient;
import java.util.List;

public class AlertGeneratorTest {
    
    @Test
    void testBloodPressureAlerts() {
        DataStorage dataStorage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);
        // Test for Increasing Trend
        // Verify an alert is triggered when three consecutive blood pressure readings 
        // increase by more than 10 mmHg each.

        // Set up patient and patient records with increasing trend in blood pressure
        double[] bloodPressureValuesIncreasing = {120, 135, 150};

        // Add patient records to data storage
        for (int i = 0; i < bloodPressureValuesIncreasing.length; i++) {
            dataStorage.addPatientData(0, bloodPressureValuesIncreasing[i], "Systolic Blood Pressure", System.currentTimeMillis() + i * 1000); // Adding records with increasing timestamps
        }

        // Test for Decreasing Trend
        // Set up patient and patient records with increasing trend in blood pressure
        double[] bloodPressureValuesDecreasing = {150, 135, 120};

        // Add patient records to data storage
        for (int i = 0; i < bloodPressureValuesDecreasing.length; i++) {
            dataStorage.addPatientData(1, bloodPressureValuesDecreasing[i], "Systolic Blood Pressure", System.currentTimeMillis() + i * 1000); // Adding records with increasing timestamps
        }

        // Test for Critical Thresholds
        // Systolic blood pressure exceeds 180 mmHg
        dataStorage.addPatientData(2, 200, "Systolic Blood Pressure", 0);
        // Systolic blood pressure drops below 90 mmHg
        dataStorage.addPatientData(3, 80, "Systolic Blood Pressure", 0);
        // Diastolic blood pressure exceeds 120 mmHg
        dataStorage.addPatientData(4, 130, "Diastolic Blood Pressure", 0);
        // Diastolic blood pressure drops below 60 mmHg
        dataStorage.addPatientData(5, 50, "Diastolic Blood Pressure", 0);

        // Call evaluatedata method
        List<Patient> patients = dataStorage.getAllPatients();
        for (Patient patient : patients) {
            alertGenerator.evaluateData(patient);
        }
    }

    @Test
    void testBloodSaturationAlerts() {
        DataStorage dataStorage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);
        // Test for Low Saturation
        dataStorage.addPatientData(0, 91, "Blood Saturation", 0);
    
        // Test for Rapid Drop
        dataStorage.addPatientData(1, 100, "Blood Saturation", 0);
        dataStorage.addPatientData(1, 98, "Blood Saturation", 10);
        dataStorage.addPatientData(1, 95, "Blood Saturation", 10*60*1000);

        // Call evaluatedata method
        List<Patient> patients = dataStorage.getAllPatients();
        for (Patient patient : patients) {
            alertGenerator.evaluateData(patient);
        }

    }

    @Test
    void testCombinedAlert() {
        DataStorage dataStorage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Test for Hypotensive Hypoxemia Alert
        dataStorage.addPatientData(0, 89, "Systolic Blood Pressure", 0);
        dataStorage.addPatientData(0, 91, "Blood Saturation", 0);

        alertGenerator.evaluateData(dataStorage.getAllPatients().get(0));
    }

    @Test
    void testECGAlerts() {
        DataStorage dataStorage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Test for Abnormal Heart Rate
        dataStorage.addPatientData(0, 49, "ECG", 0);
        dataStorage.addPatientData(1, 101, "ECG", 0);

        // Test for Irregular Beat Patterns
        dataStorage.addPatientData(2, 60, "ECG", 0);
        dataStorage.addPatientData(2, 80, "ECG", 1);
        
        // Call evaluatedata method
        List<Patient> patients = dataStorage.getAllPatients();
        for (Patient patient : patients) {
            alertGenerator.evaluateData(patient);
        }
    }
}
