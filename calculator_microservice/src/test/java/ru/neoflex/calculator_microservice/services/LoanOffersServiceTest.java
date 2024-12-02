package ru.neoflex.calculator_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanOffersServiceTest {

    @InjectMocks
    private LoanOffersService loanOffersService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getLoanOffersDto() {
        LoanStatementRequestDto loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setAmount(BigDecimal.valueOf(20000));
        loanStatementRequestDto.setTerm(24);
        loanStatementRequestDto.setEmail("test@mail.com");

        List<LoanOfferDto> loanOffersDto = loanOffersService.getLoanOffersDto(loanStatementRequestDto);
        Assertions.assertEquals(4, loanOffersDto.size());
    }
}