package ru.neoflex.deal_microservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.deal_microservice.kafka.KafkaProducer;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.dto.EmailMessage;
import ru.neoflex.enums.MessageTheme;

import static ru.neoflex.enums.MessageTheme.*;

@Service
@AllArgsConstructor
public class EmailMessageService {

    private final ClientService clientService;
    private final KafkaProducer kafkaProducer;

    public void searchClientAndSendMessage(Statement statement, MessageTheme theme, String text) {
        Client client = clientService.getClient(statement.getClientId());
        EmailMessage emailMessage = new EmailMessage(client.getEmail(), theme,
                statement.getId(), text);
        ObjectMapper objectMapper = new ObjectMapper();
        String emailMessageJSON;
        try {
            emailMessageJSON = objectMapper.writeValueAsString(emailMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String topicName = null;
        if (theme == FINISH_REGISTRATION) {
            topicName = "finish-registration";
        } else if (theme == CREATE_DOCUMENTS) {
            topicName = "create-documents";
        } else if (theme == SEND_DOCUMENTS) {
            topicName = "send-documents";
        } else if (theme == SEND_SES) {
            topicName = "send-ses";
        } else if (theme == CREDIT_ISSUED) {
            topicName = "credit-issued";
        } else if (theme == STATEMENT_DENIED) {
            topicName = "statement-denied";
        }
        kafkaProducer.sendMessage(topicName, emailMessageJSON);
    }
}
