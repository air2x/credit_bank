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
        String topicName = theme.toString().toLowerCase();
        kafkaProducer.sendMessage(topicName, emailMessageJSON);
    }
}
