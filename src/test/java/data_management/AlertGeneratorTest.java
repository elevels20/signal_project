package data_management;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.alerts.AlertGenerator;
import com.alerts.Pager;
import com.alerts.PagerAlertHandler;
import com.data_management.Patient;

import java.util.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.alerts.*;
public class AlertGeneratorTest {

    private static DataStorage dataStorage;

    @BeforeAll
    static void setUp() {
        // Set up the singleton instance of DataStorage
        dataStorage = DataStorage.getInstance();
    }

    @Test
    void testEvaluateData() {
        // Create sample patient data
        Patient patient = new Patient(1);
        patient.addRecord(190, "Systolic Blood Pressure", System.currentTimeMillis());
        patient.addRecord( 150, "ECG", System.currentTimeMillis());
        patient.addRecord(90, "Blood Saturation", System.currentTimeMillis());

        dataStorage.addPatientData(1,190, "Systolic Blood Pressure", System.currentTimeMillis());
        dataStorage.addPatientData(1,150, "ECG", System.currentTimeMillis());
        dataStorage.addPatientData(1,90, "Blood Saturation", System.currentTimeMillis());

        // Create AlertGenerator instance
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Create a custom AlertHandler for testing
        List<Alert> alerts = new ArrayList<>();
        int[] alertCount = {0};

        alertGenerator.getAlertHandlers().clear();
        AlertHandler alertHandler = alert -> {
            alerts.add(alert);
            alertCount[0]++;
        };
        alertGenerator.getAlertHandlers().add(alertHandler);

        // Call evaluateData method
        alertGenerator.evaluateData(patient);
        // Verify the generated alerts
        assertEquals(3, alertCount[0]); // Expecting one alert per patient record

    }
}