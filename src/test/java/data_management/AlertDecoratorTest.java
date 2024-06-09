package data_management;

import static org.junit.jupiter.api.Assertions.*;
import
import com.alerts.Alert;
import com.alerts.PriorityAlertDecorator;
import com.alerts.RepeatedAlertDecorator;
import org.junit.jupiter.api.Test;

public class AlertDecoratorTest {

    @Test
    public void testRepeatedAlertDecorator() {
        Alert alert = new Alert("patient1", "Condition", System.currentTimeMillis());
        Alert repeatedAlert = new RepeatedAlertDecorator(alert, 2, 5);

        assertNotNull(repeatedAlert);
        assertTrue(repeatedAlert.getCondition().contains("Condition (Repeated)"));
    }

    @Test
    public void testPriorityAlertDecorator() {
        Alert alert = new Alert("patient1", "Condition", System.currentTimeMillis());
        Alert priorityAlert = new PriorityAlertDecorator(alert, "HIGH");

        assertNotNull(priorityAlert);
        assertTrue(priorityAlert.getCondition().contains("Condition (High Priority)"));
    }


}
