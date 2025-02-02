package ru.neoflex.deal_microservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Random;
import java.util.UUID;

import static ru.neoflex.enums.ApplicationStatus.*;
import static ru.neoflex.enums.ChangeType.AUTOMATIC;
import static ru.neoflex.enums.CreditStatus.CALCULATED;
import static ru.neoflex.enums.MessageTheme.*;
import static ru.neoflex.enums.MessageTheme.CREDIT_ISSUED;

@Service
@Slf4j
@AllArgsConstructor
public class StatementService {

    private final StatementRepository statementRepository;
    private final EmailMessageService emailMessageService;

    public Statement createStatement(Client client) {
        if (client == null) {
            throw new MSDealException("Client with statementId cannot be null");
        }
        Statement statement = new Statement();
        statement.setClientId(client.getId());
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

        emailMessageService.searchClientAndSendMessage(statement, FINISH_REGISTRATION, "Завершите регистрацию");
    }

    public Statement getStatement(UUID statementId) {
        return statementRepository.getReferenceById(statementId);
    }

    public void sendCreateDoc(String statementId) {
        Statement statement = getStatement(UUID.fromString(statementId));
        emailMessageService.searchClientAndSendMessage(statement, SEND_DOCUMENTS, "Сформированные документы ");
    }

    @Transactional
    public void createAndSaveSesCode(String statementId) {
        Statement statement = getStatement(UUID.fromString(statementId));
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        statement.setSes_code(String.valueOf(randomNumber));

        emailMessageService.searchClientAndSendMessage(statement, SEND_SES, "Ваш код для подписания документов \""
                + statement.getSes_code() + "\"");
    }

    public void checkCode(String statementId, String code) {
        Statement statement = getStatement(UUID.fromString(statementId));
        if (code.equals(statement.getSes_code())) {
            emailMessageService.searchClientAndSendMessage(statement, CREDIT_ISSUED, "Документы успешно подписаны");
        } else {
            throw new MSDealException("Неверный код");
        }
        addStatementStatusHistory(statement, ApplicationStatus.CREDIT_ISSUED, AUTOMATIC);
    }
}
