package dossier_microservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dossier_microservice.services.EmailMessageService;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.neoflex.dto.EmailMessage;

@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final EmailMessageService emailMessageService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "finish-registration", groupId = "email-group")
    public void consumeFinishRegistrationEmail(ConsumerRecord<String, String> record) {
        String emailMessageJSON = record.value();
        EmailMessage emailMessage;
        try {
            emailMessage = objectMapper.readValue(emailMessageJSON, EmailMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        emailMessageService.sendEmail(emailMessage.getAddress(), emailMessage.getTheme().toString(), emailMessage.getText());
    }


    @KafkaListener(topics = "create-documents", groupId = "email-group")
    public void consumeCreateDocumentsEmail(ConsumerRecord<String, String> record) {

    }

    @KafkaListener(topics = "send-documents", groupId = "email-group")
    public void consumeSendDocumentsEmail(ConsumerRecord<String, String> record) {
    }

    @KafkaListener(topics = "send-ses", groupId = "email-group")
    public void consumeSendSesEmail(ConsumerRecord<String, String> record) {
    }

    @KafkaListener(topics = "credit-issued", groupId = "email-group")
    public void consumeCreditIssuedEmail(ConsumerRecord<String, String> record) {
    }

    @KafkaListener(topics = "statement-denied", groupId = "email-group")
    public void consumeStatementDeniedEmail(ConsumerRecord<String, String> record) {
    }
}
