package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Credit;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.deal_microservice.repositories.CreditRepository;
import ru.neoflex.dto.CreditDto;
import ru.neoflex.dto.EmploymentDto;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.neoflex.enums.ApplicationStatus.APPROVED;
import static ru.neoflex.enums.ChangeType.AUTOMATIC;
import static ru.neoflex.enums.EmploymentPosition.WORKER;
import static ru.neoflex.enums.Gender.MALE;
import static ru.neoflex.enums.MaritalStatus.SINGLE;

class CreditServiceTest {

    @Mock
    private CreditRepository creditRepository;
    @Mock
    private StatementService statementService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private CreditService creditService;

    private Statement statement;
    private CreditDto creditDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        creditDto = new CreditDto();
        creditDto.setAmount(BigDecimal.valueOf(500000));
        creditDto.setTerm(15);
        creditDto.setIsInsuranceEnabled(true);
        creditDto.setIsSalaryClient(true);
        creditDto.setPsk(BigDecimal.valueOf(25));
        creditDto.setMonthlyPayment(BigDecimal.valueOf(25000));
        creditDto.setPaymentSchedule(new ArrayList<>());

        statement.setStatementId(UUID.randomUUID());
    }

    @Test
    void createCredit() {

    }

    @Test
    void createTestCreditIfCreditDtoIsNull() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () -> creditService.createCredit(null, statement.getStatementId()));
        Assertions.assertEquals("CreditDto cannot be null", ex.getMessage());
    }
}