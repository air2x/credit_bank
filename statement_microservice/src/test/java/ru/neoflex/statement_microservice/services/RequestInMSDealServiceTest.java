package ru.neoflex.statement_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.statement_microservice.exceptions.MSStatementException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class RequestInMSDealServiceTest {

    @Mock
    private FeignClientRequestInMSDeal feignClientRequestInMSDeal;

    @InjectMocks
    private RequestInMSDealService requestInMSDealService;

    private UUID id;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
    }

    @Test
    void getLoanOffersTest() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        requestDto.setEmail("test@mail.com");

        LoanOfferDto loanOffer = new LoanOfferDto();
        loanOffer.setStatementId(id);

        List<LoanOfferDto> expectedOffers = new ArrayList<>();
        expectedOffers.add(loanOffer);
        expectedOffers.add(loanOffer);
        expectedOffers.add(loanOffer);
        expectedOffers.add(loanOffer);

        when(feignClientRequestInMSDeal.offers(requestDto)).thenReturn(expectedOffers);

        List<LoanOfferDto> actualOffers = requestInMSDealService.getLoanOffers(requestDto);

        Assertions.assertNotNull(actualOffers);
        Assertions.assertEquals(4, actualOffers.size());
        Assertions.assertEquals(id, actualOffers.get(0).getStatementId());
        Mockito.verify(feignClientRequestInMSDeal, times(1)).offers(requestDto);
    }

    @Test
    void getLoanOffersTestIfLoanStatementRequestDtoIsNull() {
        Exception ex = Assertions.assertThrows(MSStatementException.class, () -> requestInMSDealService.getLoanOffers(null));
        Assertions.assertEquals("LoanStatementRequestDto cannot be null", ex.getMessage());
    }

    @Test
    void addLoanOfferInStatementTest() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(id);
        requestInMSDealService.addLoanOfferInStatement(loanOfferDto);
        verify(feignClientRequestInMSDeal, times(1)).offers(loanOfferDto);
    }

    @Test
    void addLoanOfferInStatementIfLoanOfferDtoIsNull() {
        Exception ex = Assertions.assertThrows(MSStatementException.class, () -> requestInMSDealService.addLoanOfferInStatement(null));
        Assertions.assertEquals("LoanOfferDto cannot be null", ex.getMessage());
    }
}