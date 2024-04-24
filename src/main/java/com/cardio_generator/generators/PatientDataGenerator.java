package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interace for generating patient data.
 */
public interface PatientDataGenerator {
    /**
     * 
     * @param patientId The integer patient id for which to generate data
     * @param outputStrategy The output strategy used for the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
