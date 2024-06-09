package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DataStorageTest {

    private DataStorage dataStorage;

    @BeforeEach
    public void setUp() {
        dataStorage = DataStorage.getInstance();
    }

    @Test
    public void testAddPatientDataAndRetrieveRecords() {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Temperature";
        long timestamp = System.currentTimeMillis();

        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

        List<PatientRecord> records = dataStorage.getRecords(patientId, timestamp - 1000, timestamp + 1000);

        assertEquals(1, records.size());
        PatientRecord record = records.get(0);
        assertEquals(patientId, record.getPatientId());
        assertEquals(measurementValue, record.getMeasurementValue());
        assertEquals(recordType, record.getRecordType());
        assertEquals(timestamp, record.getTimestamp());
    }

    @Test
    public void testGetAllPatients() {
        int patientId1 = 1;
        int patientId2 = 2;

        dataStorage.addPatientData(patientId1, 98.6, "Temperature", System.currentTimeMillis());
        dataStorage.addPatientData(patientId2, 120.0, "BloodPressure", System.currentTimeMillis());

        List<Patient> patients = dataStorage.getAllPatients();

        assertEquals(2, patients.size());
    }
}
