package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.StatementStatusHistoryDto;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.neoflex.enums.ApplicationStatus.APPROVED;
import static ru.neoflex.enums.ApplicationStatus.DOCUMENT_CREATED;
import static ru.neoflex.enums.ChangeType.AUTOMATIC;
import static ru.neoflex.enums.ChangeType.MANUAL;
import static ru.neoflex.enums.CreditStatus.CALCULATED;

class StatementServiceTest {

    @InjectMocks
    private StatementService statementService;

    @Mock
    private StatementRepository statementRepository;

    private Client client;
    private UUID statementId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client();
        client.setClientId(UUID.randomUUID());
        statementId = UUID.randomUUID();
    }

    @Test
    void createStatement() {
        Statement statement1 = statementService.createStatement(client, statementId);
        Assertions.assertNotNull(statement1);
        Assertions.assertEquals(statementId, statement1.getStatementId());
        Assertions.assertEquals(client.getClientId(), statement1.getClientId());
        Assertions.assertNotNull(statement1.getStatusHistory());
    }

    @Test
    void createStatementIfClientNull() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                statementService.createStatement(null, statementId));
        Assertions.assertEquals("Client cannot be null", ex.getMessage());
    }

    @Test
    void addStatementStatusHistory() {
        Statement statement = new Statement();
        List<StatementStatusHistoryDto> statementStatusHistoryDtos =
                statementService.addStatementStatusHistory(statement, DOCUMENT_CREATED,AUTOMATIC);
        Assertions.assertEquals(1, statementStatusHistoryDtos.size());
        statement.setStatusHistory(statementStatusHistoryDtos);
        List<StatementStatusHistoryDto> statementStatusHistoryDtos1 =
                statementService.addStatementStatusHistory(statement, APPROVED, MANUAL);
        Assertions.assertEquals(2, statementStatusHistoryDtos.size());
    }

    @Test
    void addStatementStatusHistoryIfStatementNull() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                statementService.addStatementStatusHistory(null, DOCUMENT_CREATED, AUTOMATIC));
        Assertions.assertEquals("Statement cannot be null", ex.getMessage());
    }
    @Test
    void addStatementStatusHistoryIfApplicationStatusNull() {
        Statement statement = new Statement();
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                statementService.addStatementStatusHistory(statement, null, AUTOMATIC));
        Assertions.assertEquals("ApplicationStatus cannot be null", ex.getMessage());
    }
    @Test
    void addStatementStatusHistoryIfChangeTypeNull() {
        Statement statement = new Statement();
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                statementService.addStatementStatusHistory(statement, DOCUMENT_CREATED, null));
        Assertions.assertEquals("ChangeType cannot be null", ex.getMessage());
    }

    @Test
    void saveStatement() {
        Statement statement = new Statement();
        statementService.saveStatement(statement);
        verify(statementRepository).save(statement);
    }

    @Test
    void addLoanOfferInStatement() {
        Statement statement = new Statement();
        statement.setStatementId(statementId);
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);

        when(statementRepository.getReferenceById(loanOfferDto.getStatementId())).thenReturn(statement);
        statementService.addLoanOfferInStatement(loanOfferDto);
        Assertions.assertEquals(loanOfferDto, statement.getAppliedOffer());
        Assertions.assertEquals(CALCULATED, statement.getStatus());
        verify(statementRepository).save(statement);
    }

    @Test
    void getStatement() {
        Statement statement = new Statement();
        statementService.getStatement(statement.getStatementId());
        verify(statementRepository).getReferenceById(statement.getStatementId());
    }
}