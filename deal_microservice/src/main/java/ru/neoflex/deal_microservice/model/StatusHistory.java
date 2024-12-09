package ru.neoflex.deal_microservice.model;

import ru.neoflex.deal_microservice.enums.ChangeType;
import ru.neoflex.deal_microservice.enums.CreditStatus;

import java.time.LocalDate;

public class StatusHistory {
    private CreditStatus status;
    private LocalDate time;
    private ChangeType changeType;
}
