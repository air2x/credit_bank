package ru.neoflex.deal_microservice.model;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "passport")
public class Passport {
    private UUID passport_uuid;
    private String series;
    private String number;
    private String issueBranch;
    private String issueDate;
}
