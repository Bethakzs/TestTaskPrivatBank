package org.example.testtaskprivatbank.notification;

import org.example.testtaskprivatbank.model.Task;

public interface NotificationStrategy {

    boolean shouldNotify(Task task);

    void markNotified(Task task);
}

