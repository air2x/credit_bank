package ru.neoflex.deal_microservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.StatementStatusHistoryDto;
import ru.neoflex.enums.ApplicationStatus;
import ru.neoflex.enums.ChangeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.neoflex.enums.ApplicationStatus.DOCUMENT_CREATED;
import static ru.neoflex.enums.ApplicationStatus.PREAPPROVAL;
import static ru.neoflex.enums.ChangeType.AUTOMATIC;
import static ru.neoflex.enums.CreditStatus.CALCULATED;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatementService {

    private final StatementRepository statementRepository;

    public Statement createStatement(Client client, UUID statementId) {
        if (client == null) {
            throw new MSDealException("Client cannot be null");
        }
        Statement statement = new Statement();
        statement.setStatementId(statementId);
        statement.setClientId(client.getClientId());
        statement.setCreationDate(LocalDate.now());
        statement.setStatusHistory(addStatementStatusHistory(statement, DOCUMENT_CREATED, AUTOMATIC));
        return statement;
    }

    public List<StatementStatusHistoryDto> addStatementStatusHistory(Statement statement, ApplicationStatus status, ChangeType changeType) {
        if (statement == null) {
            throw new MSDealException("Statement cannot be null");
        }
        if (status == null) {
            throw new MSDealException("ApplicationStatus cannot be null");
        }
        if (changeType == null) {
            throw new MSDealException("ChangeType cannot be null");
        }
        List<StatementStatusHistoryDto> statusHistory = statement.getStatusHistory();
        if (statusHistory == null) {
            statusHistory = new ArrayList<>();
            statusHistory.add(new StatementStatusHistoryDto(DOCUMENT_CREATED, LocalDateTime.now(), AUTOMATIC));
        } else {
            statusHistory.add(new StatementStatusHistoryDto(status, LocalDateTime.now(), changeType));
        }
        return statusHistory;
    }

    public void saveStatement(Statement statement) {
        statementRepository.save(statement);
    }

    @Transactional
    public void addLoanOfferInStatement(LoanOfferDto loanOfferDto) {
        Statement statement = getStatement(loanOfferDto.getStatementId());
        statement.setAppliedOffer(loanOfferDto);
        statement.setStatus(CALCULATED);
        statement.setStatusHistory(addStatementStatusHistory(statement, PREAPPROVAL, AUTOMATIC));
        saveStatement(statement);
    }

    public Statement getStatement(UUID statementId) {
        return statementRepository.getReferenceById(statementId);
    }
}
