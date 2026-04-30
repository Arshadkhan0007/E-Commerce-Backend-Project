package com.example.NotificationService.service;

import com.example.NotificationService.dto.SimpleEmailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class NotificationService {

    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000)
    )
    @KafkaListener(topics = "registration-topic", groupId = "registration-notification-handlers")
    @Async("taskExecutor")
    public void sendSimpleMail(String email) {

        SimpleEmailDetails simpleEmailDetails = new SimpleEmailDetails(
                email,
                "Successfully registered to our E-Commerce application",
                "Welcome, glad to have you on board, Your email: " + email + " has been registered successfully on " + LocalDate.now()
                );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(simpleEmailDetails.getTo());
        message.setSubject(simpleEmailDetails.getSubject());
        message.setText(simpleEmailDetails.getBody());

        try {
            javaMailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send the mail. Exception: {}, Reason: {}", ex, ex.getMessage());
        }
    }

    @DltHandler
    public void listenDLT(String email, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("DLT Received : {}, from {}, offset {}", email, topic, offset);
    }

}
