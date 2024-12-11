package ru.neoflex.deal_microservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.enums.ChangeType;
import ru.neoflex.enums.CreditStatus;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatusHistory {
    private CreditStatus status;
    private LocalDate time;
    private ChangeType changeType;
}
