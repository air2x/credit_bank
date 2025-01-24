package dossier_microservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dossier_microservice.services.EmailMessageService;
import dossier_microservice.exceptions.MSDossierException;
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

    @KafkaListener(topics = "#{@environment.getProperty('kafka.topic.finish-registration')}",
            groupId = "#{@environment.getProperty('kafka.group.email-group')}")
    public void consumeFinishRegistrationEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }

    @KafkaListener(topics = "#{@environment.getProperty('kafka.topic.create-documents')}",
            groupId = "#{@environment.getProperty('kafka.group.email-group')}")
    public void consumeCreateDocumentsEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }

    @KafkaListener(topics = "#{@environment.getProperty('kafka.topic.send-documents')}",
            groupId = "#{@environment.getProperty('kafka.group.email-group')}")
    public void consumeSendDocumentsEmail(ConsumerRecord<String, String> record) throws MessagingException, IOException {
        EmailMessage emailMessage = parseEmailMessage(record);
        emailMessageService.sendEmailWithFile(emailMessage.getAddress(), emailMessage.getTheme().toString(), emailMessage.getText());
    }

    @KafkaListener(topics = "#{@environment.getProperty('kafka.topic.send-ses')}",
            groupId = "#{@environment.getProperty('kafka.group.email-group')}")
    public void consumeSendSesEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }

    @KafkaListener(topics = "#{@environment.getProperty('kafka.topic.credit-issued')}",
            groupId = "#{@environment.getProperty('kafka.group.email-group')}")
    public void consumeCreditIssuedEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }

    @KafkaListener(topics = "#{@environment.getProperty('kafka.topic.statement-denied')}",
            groupId = "#{@environment.getProperty('kafka.group.email-group')}")
    public void consumeStatementDeniedEmail(ConsumerRecord<String, String> record) {
        processAndSendEmail(record);
    }

    private void processAndSendEmail(ConsumerRecord<String, String> record) {
        EmailMessage emailMessage = parseEmailMessage(record);
        log.info(emailMessage.getAddress() + " получен из ms-deal");
        if (emailMessage.getTheme().toString() == null) {
            throw new MSDossierException("Theme can not be null");
        }
        emailMessageService.sendEmail(emailMessage.getAddress(), emailMessage.getTheme().toString(), emailMessage.getText());
        log.info(emailMessage.getText() + " Отправлено на " + emailMessage.getAddress());
    }

    private EmailMessage parseEmailMessage(ConsumerRecord<String, String> record) {
        EmailMessage emailMessage;
        if (record == null) {
            throw new MSDossierException("Record can not be null");
        }
        try {
            emailMessage = objectMapper.readValue(record.value(), EmailMessage.class);
        } catch (JsonProcessingException e) {
            log.warn("Ошибка: " + e.getMessage());
            throw new MSDossierException(e.getMessage());
        }
        return emailMessage;
    }
}
