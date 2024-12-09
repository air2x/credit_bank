package ru.neoflex.deal_microservice.model;

import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public class Passport {
    private UUID passport_uuid;

    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта состоит из 4 цифр")
    private String series;

    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта состоит из 6 цифр")
    private String number;

    private String issueBranch;
    private String issueDate;
}
