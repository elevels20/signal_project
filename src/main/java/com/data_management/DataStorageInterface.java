package com.data_management;

import java.util.List;
public interface DataStorageInterface {
    List<PatientRecord> getRecords(int patientId, long startTime, long endTime);
    public List<Patient> getAllPatients();
}
