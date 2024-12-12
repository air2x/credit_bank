package ru.neoflex.deal_microservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.enums.CreditStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "statement")
public class Statement {

    @Id
    @Column(name = "statement_id")
    private UUID statementId;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "credit_id")
    private UUID creditId;

    @Column(name = "status")
    private CreditStatus status;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "applied_offer")
    @JdbcTypeCode(SqlTypes.JSON)
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private LocalDate signDate;

    @Column(name = "ses_code")
    private String ses_code;

    @Column(name = "status_history")
    @JdbcTypeCode(SqlTypes.JSON)
    private StatusHistory statusHistory;
}
