package ru.neoflex.deal_microservice.model;

import ru.neoflex.deal_microservice.enums.Gender;
import ru.neoflex.deal_microservice.enums.MaritalStatus;

import java.time.LocalDate;
import java.util.UUID;

public class Client {
    private UUID clientId;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private int dependentAmount;
    private UUID passportId;
    private UUID employmentId;
    private String accountNumber;
}
