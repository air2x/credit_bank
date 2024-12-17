package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.RestClient;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.*;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.deal_microservice.repositories.CreditRepository;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.ScoringDataDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FinishRegistrationServiceTest {

    @InjectMocks
    private FinishRegistrationService finishRegistrationService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private StatementRepository statementRepository;
    @Mock
    private CreditRepository creditRepository;
    @Mock
    private RestClient restClient;
    @Mock
    ModelMapper mapper;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;
    private String statementId;
    private Statement statement;
    private Client client;
    private LoanOfferDto loanOfferDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        statementId = UUID.randomUUID().toString();
        finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        statement = new Statement();
        statement.setStatementId(UUID.fromString(statementId));
        statement.setClientId(UUID.randomUUID());
        client = new Client();
        client.setClientId(statement.getClientId());
        Passport passport = new Passport(UUID.randomUUID(), "1234", "123456", "MVD", LocalDate.now());
        client.setPassportId(passport);
        client.setFirstName("Nikita");
        client.setLastName("Bibin");
        client.setEmploymentId(new Employment());

        loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(UUID.randomUUID());
        loanOfferDto.setTerm(12);
        loanOfferDto.setIsInsuranceEnabled(true);
        loanOfferDto.setIsSalaryClient(true);
        loanOfferDto.setRate(BigDecimal.valueOf(20));
        loanOfferDto.setMonthlyPayment(BigDecimal.valueOf(15000));
        loanOfferDto.setRequestAmount(BigDecimal.valueOf(500000));
        loanOfferDto.setTotalAmount(BigDecimal.valueOf(550000));
        statement.setAppliedOffer(loanOfferDto);
    }

    @Test
    void testFinallyRegistersFinishRegistrationRequestDtoIsNull() {
        Exception exception = assertThrows(MSDealException.class, () -> {
            finishRegistrationService.finallyRegisters(null, statementId);
        });
        assertEquals("FinishRegistrationRequestDto is not be null", exception.getMessage());
    }

    @Test
    void testFinallyRegistersStatementIdIsNull() {
        Exception exception = assertThrows(MSDealException.class, () -> {
            finishRegistrationService.finallyRegisters(finishRegistrationRequestDto, null);
        });
        assertEquals("StatementId is not be null", exception.getMessage());
    }

    @Test
    void testFinallyRegistersStatementNotFound() {
        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        Exception exception = assertThrows(MSDealException.class, () -> {
            finishRegistrationService.finallyRegisters(finishRegistrationRequestDto, statementId);
        });
        assertEquals("Statement with id " + statementId + " was not be find", exception.getMessage());
    }

    @Test
    void testCreateScoringDataDto() {
        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.of(statement));
        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.of(client));
        Assertions.assertNotNull(finishRegistrationService.createScoringDataDto(finishRegistrationRequestDto, statementId));
        ScoringDataDto scoringDataDto = finishRegistrationService.createScoringDataDto(finishRegistrationRequestDto, statementId);
        assertNotNull(scoringDataDto);
        assertEquals(loanOfferDto.getRequestAmount(), scoringDataDto.getAmount());
        assertEquals(loanOfferDto.getTerm(), scoringDataDto.getTerm());
        assertEquals(loanOfferDto.getIsSalaryClient(), scoringDataDto.getIsSalaryClient());
        assertEquals(loanOfferDto.getIsInsuranceEnabled(), scoringDataDto.getIsInsuranceEnabled());
    }

    @Test
    void testCreateScoringDataDtoClientNotFound() {
        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(statement));
        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        Exception exception = assertThrows(MSDealException.class, () -> {
            finishRegistrationService.createScoringDataDto(finishRegistrationRequestDto, statementId);
        });
        assertEquals("Client with id " + statement.getClientId() + " was not be find", exception.getMessage());
    }
}