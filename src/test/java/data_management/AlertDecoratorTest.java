package data_management;

import static org.junit.jupiter.api.Assertions.*;
import com.alerts.Alert;
import com.alerts.PriorityAlertDecorator;
import com.alerts.RepeatedAlertDecorator;
import org.junit.jupiter.api.Test;

public class AlertDecoratorTest {

    @Test
    public void testRepeatedAlertDecorator() {
        Alert alert = new Alert("patient1", "Condition", 5);
        Alert repeatedAlert = new RepeatedAlertDecorator(alert, 2, 5);
        assertNotNull(repeatedAlert);
        assertTrue(repeatedAlert.getAlertMessage().contains("Condition for patient patient1 at 5\n" +
                "Repeated 2 times at intervals of 5 ms."));
    }

    @Test
    public void testPriorityAlertDecorator() {
        Alert alert = new Alert("patient1", "Condition", 5);
        Alert priorityAlert = new PriorityAlertDecorator(alert, "HIGH");
        assertNotNull(priorityAlert);
        assertTrue(priorityAlert.getAlertMessage().contains("[Priority: HIGH] Alert: Condition for patient patient1 at 5"));
    }


}
