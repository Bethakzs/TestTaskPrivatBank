package org.example.testtaskprivatbank.notification.time;

import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.notification.NotificationStrategy;

public class DeadlineNotificationStrategy implements NotificationStrategy {

    @Override
    public boolean shouldNotify(Task task) {
        return !task.isNotifiedDeadline();
    }

    @Override
    public void markNotified(Task task) {
        task.setNotifiedDeadline(true);
    }
}
