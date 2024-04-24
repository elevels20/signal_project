package com.cardio_generator.outputs;

/**
 * Interface for defining output strategies.
 */
public interface OutputStrategy {
    /**
     * Outputs the data that is associated with a certain patient Id.
     * 
     * @param patientId The integer patient id for which to generate output
     * @param timestamp The timestamp of the data as a long type
     * @param label The string label of the data
     * @param data The data as a string that needs outputting
     */
    void output(int patientId, long timestamp, String label, String data);
}
