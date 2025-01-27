package ru.neoflex.calculator_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.neoflex.calculator_microservice.util.exceptions.NullEmailException;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.util.List;

import static ru.neoflex.calculator_microservice.services.LoanOffersService.*;


class LoanOffersServiceTest {

    @InjectMocks
    private LoanOffersService loanOffersService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getLoanOffersDtoTest() {
        LoanStatementRequestDto loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setAmount(BigDecimal.valueOf(20000));
        loanStatementRequestDto.setTerm(24);
        loanStatementRequestDto.setEmail("test@mail.com");

        List<LoanOfferDto> loanOffersDto = loanOffersService.getLoanOffersDto(loanStatementRequestDto);
        Assertions.assertNotNull(loanOffersDto);
        Assertions.assertEquals(4, loanOffersDto.size());
        for (LoanOfferDto l : loanOffersDto) {
            Assertions.assertEquals(24, l.getTerm());
            Assertions.assertNotNull(l.getRate());
            Assertions.assertNotNull(l.getStatementId());
            Assertions.assertNotNull(l.getMonthlyPayment());
            Assertions.assertNotNull(l.getIsInsuranceEnabled());
            Assertions.assertNotNull(l.getStatementId());
            Assertions.assertNotNull(l.getRequestAmount());
            Assertions.assertNotNull(l.getTotalAmount());

            if (l.getRate() == RATE_35) {
                Assertions.assertEquals(false, l.getIsSalaryClient());
                Assertions.assertEquals(false, l.getIsInsuranceEnabled());
            } else if (l.getRate() == BASE_RATE_25) {
                Assertions.assertEquals(false, l.getIsSalaryClient());
                Assertions.assertEquals(true, l.getIsInsuranceEnabled());
            } else if (l.getRate() == RATE_23) {
                Assertions.assertEquals(true, l.getIsSalaryClient());
                Assertions.assertEquals(false, l.getIsInsuranceEnabled());
            } else if (l.getRate() == RATE_20) {
                Assertions.assertEquals(true, l.getIsSalaryClient());
                Assertions.assertEquals(true, l.getIsInsuranceEnabled());
            }
        }
    }

    @Test
    void getLoanOffersDtoIfEmailNullTest() {
        LoanStatementRequestDto loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setAmount(BigDecimal.valueOf(20000));
        loanStatementRequestDto.setTerm(24);
        loanStatementRequestDto.setEmail(null);

        Exception ex = Assertions.assertThrows(NullEmailException.class, () -> loanOffersService.getLoanOffersDto(loanStatementRequestDto));
        Assertions.assertEquals("Email is not be null", ex.getMessage());
    }
}