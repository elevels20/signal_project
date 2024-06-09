package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.*;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AlertFactoryTest {
    @Test
    public void testCreateBloodPressureAlert() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("patient1", "High", System.currentTimeMillis());
        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("Blood Pressure: High"));
    }

    @Test
    public void testCreateBloodSaturationAlert() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("patient1", "Low", System.currentTimeMillis());
        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("Blood Oxygen: Low"));
    }

    @Test
    public void testCreateCombinedAlert() {
        AlertFactory factory = new CombinedAlertFactory();
        Alert alert = factory.createAlert("patient1", "Critical", System.currentTimeMillis());

        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("Blood pressure + Blood saturation: Critical"));
    }

    @Test
    public void testCreateECGAlert() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("patient1", "Critical", System.currentTimeMillis());

        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("ECG: Critical"));
    }
}