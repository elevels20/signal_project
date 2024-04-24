package com.cardio_generator.generators;

// All non-static imports should be in one block
import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    // Changed constant variable name to uppercase letters with underscores
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name to lowerCamelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

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
