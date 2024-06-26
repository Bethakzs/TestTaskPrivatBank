package org.example.testtaskprivatbank.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component // Method which listen kafka topic (it can be any logic in your project)
public class DeadlineConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DeadlineConsumer.class);

    @KafkaListener(topics = "${variables.topic.deadline}", groupId = "deadline-group")
    public void listen(String message) {
        System.out.println("Received Message: " + message);
        logger.info("Received Message from Kafka: {}", message);
    }
}
