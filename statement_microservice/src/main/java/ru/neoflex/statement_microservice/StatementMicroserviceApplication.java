package ru.neoflex.statement_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StatementMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatementMicroserviceApplication.class, args);
	}

}
