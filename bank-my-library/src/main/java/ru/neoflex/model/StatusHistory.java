package ru.neoflex.model;

import ru.neoflex.enums.ChangeType;
import ru.neoflex.enums.CreditStatus;

import java.time.LocalDate;

public class StatusHistory {
    private CreditStatus status;
    private LocalDate time;
    private ChangeType changeType;
}
