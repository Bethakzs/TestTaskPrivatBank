package org.example.testtaskprivatbank.notification.time;

import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.notification.NotificationStrategy;

public class OneHourNotificationStrategy implements NotificationStrategy {

    @Override
    public boolean shouldNotify(Task task) {
        return !task.isNotifiedOneHour();
    }

    @Override
    public void markNotified(Task task) {
        task.setNotifiedOneHour(true);
    }
}
