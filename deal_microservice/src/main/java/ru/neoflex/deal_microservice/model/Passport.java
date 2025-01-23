package ru.neoflex.deal_microservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Passport {
    private UUID passport_uuid;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
