package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanStatementRequestDto;

class RequestInMSCalcServiceTest {

    @InjectMocks
    private RequestInMSCalcService requestInMSCalcService;

    @Mock
    private CreditService creditService;

    private LoanStatementRequestDto loanStatementRequestDto;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanStatementRequestDto = new LoanStatementRequestDto();
        finishRegistrationRequestDto = new FinishRegistrationRequestDto();
    }

    @Test
    void getLoanOffers() {

    }

    @Test
    void getLoanOffersIfLoanStatementRequestDtoIsNull() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                requestInMSCalcService.getLoanOffers(null));
        Assertions.assertEquals("LoanStatementRequestDto cannot be null", ex.getMessage());
    }

    @Test
    void calculateFinish() {
    }

    @Test
    void calculateFinishIfFinishRegistrationRequestDtoISNull() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                requestInMSCalcService.calculateFinish(null, "123"));
        Assertions.assertEquals("FinishRegistrationRequestDto cannot be null", ex.getMessage());
    }

    @Test
    void calculateFinishIfStatementIdISNull() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () ->
                requestInMSCalcService.calculateFinish(finishRegistrationRequestDto, null));
        Assertions.assertEquals("StatementId cannot be null", ex.getMessage());
    }
}