package com.cardio_generator.generators;

// All non-static imports should be in one block
import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Represents a patient data generator for alert data simulations.
 * This class is responsible for generating randomized patient data.
 * This class implements PatientDataGenerator.
 */
public class AlertGenerator implements PatientDataGenerator {

    // Changed constant variable name to uppercase letters with underscores
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name to lowerCamelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates an AlertGenerator as a boolean array for a specified number of patients.
     * This is a constructor method.
     * 
     * @param patientCount The integer that specifies the number of patients for whom the data needs to be generated
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * {@inheritDoc}
     * 
     * Generates alert data for a specific patient and outputs it using the provided output strategy.  
     * The method simulates the occurrence and resolution of alerts based on predefined probabilities.
     * If an alert is triggered, it is output as "triggered"; if an alert is resolved, it is output as "resolved".
     *     
     * @throws Exception if an error occurs while generating blood saturation data for a patient
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Changed variable name to lowerCamelCase
            if (alertStates[patientId]) {
                // Changed constant variable name to uppercase letters with underscores
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    // Changed variable name to lowerCamelCase
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name to lowerCamelCase
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                // Changed variable name to lowerCamelCase
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                // Changed constant variable name to uppercase letters with underscores
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    // Changed variable name to lowerCamelCase
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            // Added comment to explain catch block
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
