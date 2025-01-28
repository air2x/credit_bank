package ru.neoflex.api_gateway_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiGatewayMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayMicroserviceApplication.class, args);
    }

}
