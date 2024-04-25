package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Represents a patient data generator for blood saturation data simulations.
 * This class is responsible for generating randomized patient data, for blood saturation.
 * It utilizes a pseudo-random number generator to simulate real-time patient data variations.
 * This class implements PatientDataGenerator.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /**
     * Creates a BloodSaturationDataGenerator as an integer array for a speficied number of patients.
     * The method uses a ranom number generator for simulating the data.
     * This is a constructor method.
     * 
     * @param patientCount The integer that specifies the number of patients for whom the data needs to be generated
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Generates blood saturation data for a specific patient and outputs it using the provided output strategy.  
     * 
     *  @throws Exception if an error occurs while generating blood saturation data for a patient
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
