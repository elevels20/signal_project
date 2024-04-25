package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Strategy for outputting data to files.
 * This class implements OutputStrategy and is responsible for exporting data to files.
 */
// Changed class name to UpperCamelCase
public class FileOutputStrategy implements OutputStrategy {

    // Changed variable name to lowerCamelCase
    private String baseDirectory;

    public final ConcurrentHashMap<String, String> file_map = new ConcurrentHashMap<>();
    
    /**
     * Creates a FileOutputStrategy instance with the specified base directory.
     * This is a constructor method.
     * 
     * @param baseDirectory The base directory as a string where the file will be output
     */
    // Constructor should have the same name as the class, and thus it should be int UpperCamelCase
    public FileOutputStrategy(String baseDirectory) {

        // Changed variable name to lowerCamelCase
        this.baseDirectory = baseDirectory;
    }

    /**
     * {@inheritDoc}
     * 
     * Outputs patient data to a file in a specified format.
     * The method creates a new base directory if it doesn't exist.
     * The method then appends the data to the file associated with the given label.
     * 
     * @throws IOException If an I/O error occurs while creating the base directory
     * @throws IOException If an I/O error occurs while writing the data to the file
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            // Changed variable name to lowerCamelCase
            Files.createDirectories(Paths.get(baseDirectory)); 
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // Changed variable name to lowerCamelCase
        String FilePath = file_map.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(FilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + FilePath + ": " + e.getMessage());
        }
    }
}