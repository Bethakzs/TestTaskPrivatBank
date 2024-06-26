package org.example.testtaskprivatbank.notification;

import lombok.RequiredArgsConstructor;
import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.example.testtaskprivatbank.notification.time.DeadlineNotificationStrategy;
import org.example.testtaskprivatbank.notification.time.OneHourNotificationStrategy;
import org.example.testtaskprivatbank.notification.time.TenMinutesNotificationStrategy;
import org.example.testtaskprivatbank.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeadlineMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(DeadlineMonitorService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TaskRepository taskRepository;

    @Value("${variables.topic.deadline}")
    private String deadlineTopic;

    @Scheduled(fixedRate = 1000) // check every second
    public void checkDeadlinesAndSendMessages() {
        if (!isPrimaryDatabaseConnected()) {
            logger.warn("Primary database connection lost. Switched to backup database.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Find tasks with deadlines in 1 hour and 10 minutes
        LocalDateTime inOneHour = now.plusHours(1);
        LocalDateTime inTenMinutes = now.plusMinutes(10);
        List<Task> tasksInOneHour = taskRepository.findByDeadlineBetweenAndStatus(now, inOneHour, TaskStatus.TODO);
        List<Task> tasksInTenMinutes = taskRepository.findByDeadlineBetweenAndStatus(now, inTenMinutes, TaskStatus.TODO);

        // Find overdue tasks
        List<Task> overdueTasks = taskRepository.findByDeadlineBeforeAndStatus(now, TaskStatus.TODO);

        sendMessages(tasksInOneHour, "has 1 hour left until its deadline.", new OneHourNotificationStrategy());
        sendMessages(tasksInTenMinutes, "has 10 minutes left until its deadline.", new TenMinutesNotificationStrategy());
        sendMessages(overdueTasks, "has reached its deadline.", new DeadlineNotificationStrategy());
    }

    private boolean isPrimaryDatabaseConnected() {
        try {
            taskRepository.count();
            return true;
        } catch (Exception e) {
            logger.error("Error checking primary database connection: {}", e.getMessage());
            return false;
        }
    }

    private void sendMessages(List<Task> tasks, String messageSuffix, NotificationStrategy strategy) {
        for (Task task : tasks) {
            if (strategy.shouldNotify(task)) {
                String message = "Task with title '" + task.getTitle() + "' " + messageSuffix;
                kafkaTemplate.send(deadlineTopic, message);
                logger.info("Sent message to topic {}: {}", deadlineTopic, message);

                strategy.markNotified(task);
                taskRepository.save(task);
            }
        }
    }
}
