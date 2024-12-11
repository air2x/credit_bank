package ru.neoflex.deal_microservice.model;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.enums.CreditStatus;
import ru.neoflex.model.StatusHistory;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "statement")
public class Statement {
    private UUID statementId;
    private UUID clientId;
    private UUID creditId;
    private CreditStatus status;
    private LocalDate creationDate;
    //private appliedOffer
    private LocalDate signDate;
    private String ses_code;
    private StatusHistory statusHistory;
}
