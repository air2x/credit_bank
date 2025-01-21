package dossier_microservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dossier_microservice.services.EmailMessageService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.neoflex.dto.EmailMessage;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final EmailMessageService emailMessageService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "finish-registration", groupId = "email-group")
    public void consumeFinishRegistrationEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }


    @KafkaListener(topics = "create-documents", groupId = "email-group")
    public void consumeCreateDocumentsEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }

    @KafkaListener(topics = "send-documents", groupId = "email-group")
    public void consumeSendDocumentsEmail(ConsumerRecord<String, String> record) throws MessagingException, IOException {
        EmailMessage emailMessage;
        try {
            emailMessage = objectMapper.readValue(record.value(), EmailMessage.class);
        } catch (JsonProcessingException e) {
            log.info("Error: " + e);
            throw new RuntimeException(e);
        }
        emailMessageService.sendEmailWithFile(emailMessage.getAddress(), emailMessage.getTheme().toString(), emailMessage.getText());
    }

    @KafkaListener(topics = "send-ses", groupId = "email-group")
    public void consumeSendSesEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }

    @KafkaListener(topics = "credit-issued", groupId = "email-group")
    public void consumeCreditIssuedEmail(ConsumerRecord<String, String> record) {
    }

    @KafkaListener(topics = "statement-denied", groupId = "email-group")
    public void consumeStatementDeniedEmail(ConsumerRecord<String, String> record) {
    }

    private void processAndSendEmail(ConsumerRecord<String, String> record) {
        String emailMessageJSON = record.value();
        EmailMessage emailMessage;
        try {
            emailMessage = objectMapper.readValue(emailMessageJSON, EmailMessage.class);
        } catch (JsonProcessingException e) {
            log.info("Error: " + e);
            throw new RuntimeException(e);
        }
        log.info(emailMessage.getAddress() + " получен из ms-deal");
        emailMessageService.sendEmail(emailMessage.getAddress(), emailMessage.getTheme().toString(), emailMessage.getText());
        log.info(emailMessage.getText() + " Отправлено на " + emailMessage.getAddress());
    }
}
