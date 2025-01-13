package ru.neoflex.deal_microservice.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    /*
    Завести в кафке 6 топиков, соответствующие темам, по которым необходимо направить письмо на почту Клиенту:
    finish-registration
    create-documents
    send-documents
    send-ses
    credit-issued
    statement-denied
     */
    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("finish-registration").build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("create-documents").build();
    }

    @Bean
    public NewTopic topic3() {
        return TopicBuilder.name("send-documents").build();
    }

    @Bean
    public NewTopic topic4() {
        return TopicBuilder.name("send-ses").build();
    }
    @Bean
    public NewTopic topic5() {
        return TopicBuilder.name("credit-issued").build();
    }
    @Bean
    public NewTopic topic6() {
        return TopicBuilder.name("statement-denied").build();
    }
}
