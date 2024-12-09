package ru.neoflex.deal_microservice.model;

import ru.neoflex.deal_microservice.enums.CreditStatus;

import java.time.LocalDate;
import java.util.UUID;

public class Statement {
    private UUID statementId;
    private UUID clientId;
    private UUID creditId;
    private CreditStatus status;
    private LocalDate creationDate;
    //private appliedOffer
    private LocalDate signDate;
    // private ses_code
    private StatusHistory statusHistory;
}
