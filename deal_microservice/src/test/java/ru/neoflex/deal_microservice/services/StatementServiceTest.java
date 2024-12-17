package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.neoflex.enums.ApplicationStatus.PREAPPROVAL;

class StatementServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private StatementRepository statementRepository;
    @Mock
    private RestClient restClient;

    @InjectMocks
    private StatementService statementService;

    private LoanStatementRequestDto loanStatementRequestDto;
    private Client client;
    private Statement statement;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setLastName("Nikita");
        loanStatementRequestDto.setFirstName("Bibin");
        loanStatementRequestDto.setMiddleName("Bib");
        loanStatementRequestDto.setBirthday(LocalDate.of(1990, 1, 1));
        loanStatementRequestDto.setEmail("Nikita@mail.com");
        loanStatementRequestDto.setPassportSeries("1234");
        loanStatementRequestDto.setPassportNumber("567890");
        loanStatementRequestDto.setAmount(BigDecimal.valueOf(500000));
        loanStatementRequestDto.setTerm(15);
        client = new Client();
        client.setClientId(UUID.randomUUID());

        statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
    }

    @Test
    void testGetLoanOffersDto() {
        List<LoanOfferDto> offers = statementService.getLoanOffersDto(loanStatementRequestDto);

        assertNotNull(offers);
        assertEquals(4, offers.size());

        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
    }

    @Test
    void testGetLoanOffersDtoNullRequest() {
        Exception exception = assertThrows(MSDealException.class, () -> {
            statementService.getLoanOffersDto(null);
        });
        assertEquals("LoanStatementRequestDto is not be null", exception.getMessage());
    }

    @Test
    void testSaveClient() {
        statementService.saveClient(client);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testSaveClientIsNull() {
        client = null;
        Exception exception = assertThrows(MSDealException.class, () -> {
            statementService.saveClient(client);
        });
        assertEquals("Client is not created", exception.getMessage());
    }

    @Test
    void testSaveStatement() {
        statementService.saveStatement(statement);
        verify(statementRepository, times(1)).save(statement);
    }

    @Test
    void testSaveStatementIsNull() {
        statement = null;
        Exception exception = assertThrows(MSDealException.class, () -> {
            statementService.saveStatement(statement);
        });
        assertEquals("Statement is not created", exception.getMessage());
    }

    @Test
    void testAddStatusHistory() {
        StatementService.addStatusHistory(statement, PREAPPROVAL);
        assertEquals(statement.getStatusHistory().size(), 1);
    }

    @Test
    public void testAddStatusHistoryStatementIsNull() {
        Exception exception = assertThrows(MSDealException.class, () -> {
            StatementService.addStatusHistory(null, PREAPPROVAL);
        });
        assertEquals("Statement is not be null", exception.getMessage());
    }
}