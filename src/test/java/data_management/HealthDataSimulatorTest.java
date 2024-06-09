package data_management;

import static org.junit.jupiter.api.Assertions.*;
import com.cardio_generator.HealthDataSimulator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HealthDataSimulatorTest {

    @Test
    public void testSingletonInstance() {
        HealthDataSimulator simulator1 = HealthDataSimulator.getInstance();
        HealthDataSimulator simulator2 = HealthDataSimulator.getInstance();

        assertSame(simulator1, simulator2);
    }

    @Test
    public void testInitialization() {
        String[] args = {"--patient-count", "10", "--output", "console"};
        HealthDataSimulator simulator = HealthDataSimulator.getInstance();

        try {
            simulator.initialize(args);
        } catch (IOException e) {
            fail("Initialization failed due to IOException: " + e.getMessage());
        }
    }
}
