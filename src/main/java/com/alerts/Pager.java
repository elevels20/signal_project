// package com.alerts;

// public class Pager {
//     public void sendNotification(String message) {
//         System.out.println("Pager Notification: " + message);
//     }
// }
package com.alerts;

import java.util.ArrayList;
import java.util.List;

public class Pager {
    private List<String> notifications = new ArrayList<>();

    public void sendNotification(String message) {
        notifications.add(message);
        System.out.println("Pager Notification: " + message);
    }

    public List<String> getNotifications() {
        return notifications;
    }
}
