package ru.neoflex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.enums.CreditStatus;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
