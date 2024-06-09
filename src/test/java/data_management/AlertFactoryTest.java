package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.Alert;
import com.alerts.AlertFactory;
import com.alerts.BloodPressureAlertFactory;
import org.junit.jupiter.api.Test;
import com.alerts.BloodOxygenAlertFactory;
import com.alerts.CombinedAlertFactory;

public class AlertFactoryTest {

    @Test
    public void testCreateBloodPressureAlert() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("patient1", "High", System.currentTimeMillis());

        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("Blood pressure: High"));
    }

    @Test
    public void testCreateBloodSaturationAlert() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("patient1", "Low", System.currentTimeMillis());

        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("Blood saturation: Low"));
    }

    @Test
    public void testCreateCombinedAlert() {
        AlertFactory factory = new CombinedAlertFactory();
        Alert alert = factory.createAlert("patient1", "Critical", System.currentTimeMillis());

        assertNotNull(alert);
        assertTrue(alert.getCondition().contains("Blood pressure + Blood saturation: Critical"));
    }
}

