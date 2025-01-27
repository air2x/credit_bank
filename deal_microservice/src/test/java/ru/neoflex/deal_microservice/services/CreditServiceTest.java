package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Credit;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.CreditRepository;
import ru.neoflex.dto.CreditDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        creditDto = new CreditDto();
        creditDto.setAmount(BigDecimal.valueOf(500000));
        creditDto.setTerm(15);
        creditDto.setIsInsuranceEnabled(true);
        creditDto.setIsSalaryClient(true);
        creditDto.setPsk(BigDecimal.valueOf(25));
        creditDto.setMonthlyPayment(BigDecimal.valueOf(25000));
        creditDto.setPaymentSchedule(new ArrayList<>());
        statement = new Statement();
    }

    @Test
    void createCredit() {
        when(mapper.map(creditDto, Credit.class)).thenReturn(new Credit());
        when(statementService.getStatement(any(UUID.class))).thenReturn(statement);

        creditService.createAndSaveCreditAndSaveStatement(creditDto, statement);

        verify(creditRepository).save(any(Credit.class));
        verify(statementService).saveStatement(statement);
    }

    @Test
    void createTestCreditIfCreditDtoIsNull() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                creditService.createAndSaveCreditAndSaveStatement(null, statement));
        Assertions.assertEquals("CreditDto cannot be null", ex.getMessage());
    }
}