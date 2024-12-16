package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.dto.StatementStatusHistoryDto;
import ru.neoflex.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.neoflex.enums.ApplicationStatus.PREAPPROVAL;
import static ru.neoflex.enums.ChangeType.AUTOMATIC;

class StatementServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private StatementRepository statementRepository;

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

        client = new Client();
        client.setClientId(UUID.randomUUID());

        statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
    }

    @Test
    void getLoanOffersDto() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        LoanOfferDto offer = new LoanOfferDto();
        offer.setStatementId(statement.getStatementId());
        List<LoanOfferDto> offers = statementService.getLoanOffersDto(loanStatementRequestDto);
        assertNotNull(offers);
        assertEquals(1, offers.size());
        assertEquals(statement.getStatementId(), offers.get(0).getStatementId());
    }

    @Test
    void saveClient() {
        statementService.saveClient(client);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void saveClientIsNull() {
        client = null;
        Exception exception = assertThrows(MSDealException.class, () -> {
            statementService.saveClient(client);
        });
        assertEquals("Client is not created", exception.getMessage());
    }

    @Test
    void saveStatement() {
        statementService.saveStatement(statement);
        verify(statementRepository, times(1)).save(statement);
    }

    @Test
    void saveStatementIsNull() {
        statement = null;
        Exception exception = assertThrows(MSDealException.class, () -> {
            statementService.saveStatement(statement);
        });
        assertEquals("Statement is not created", exception.getMessage());
    }

    @Test
    void addStatusHistory() {
        StatementService.addStatusHistory(statement, PREAPPROVAL);
        assertEquals(statement.getStatusHistory().size(), 1);
    }

    @Test
    public void addStatusHistoryStatementIsNull() {
        Exception exception = assertThrows(MSDealException.class, () -> {
            StatementService.addStatusHistory(null, PREAPPROVAL);
        });
        assertEquals("Statement is not be null", exception.getMessage());
    }
}