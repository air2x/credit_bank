package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanStatementRequestDto;

class RequestInMSCalcServiceTest {

    @InjectMocks
    private RequestInMSCalcService requestInMSCalcService;

    @Mock
    private RestClient restClient;

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
    void calculateFinish() {
    }
}