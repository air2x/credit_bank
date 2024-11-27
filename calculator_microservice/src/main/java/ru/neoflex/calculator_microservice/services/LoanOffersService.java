package ru.neoflex.calculator_microservice.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.util.*;

@Service
public class LoanOffersService {

    private static final Logger logger = LogManager.getLogger(LoanOffersService.class);
    public static final BigDecimal RATE_20 = BigDecimal.valueOf(20.0);
    public static final BigDecimal RATE_23 = BigDecimal.valueOf(23.0);
    public static final BigDecimal BASE_RATE_25 = BigDecimal.valueOf(25.0);
    public static final BigDecimal RATE_35 = BigDecimal.valueOf(35.0);
    public static final int MONTHS = 12;

    private final CalculateService calculateService;

    @Autowired
    public LoanOffersService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    public List<LoanOfferDto> getLoanOffersDto(LoanStatementRequestDto loanStatementRequestDto) {
        logger.info("Starting create loan offers");
        List<BigDecimal> rates = Arrays.asList(RATE_35, BASE_RATE_25, RATE_23, RATE_20);
        List<LoanOfferDto> loanOffersDto = new ArrayList<>();
        for (BigDecimal rate : rates) {
            LoanOfferDto loanOfferDto = new LoanOfferDto();
            loanOfferDto.setStatementId(UUID.randomUUID());
            loanOfferDto.setRequestAmount(loanStatementRequestDto.getAmount());
            loanOfferDto.setMonthlyPayment(calculateService.calculateMonthlyPayment(rate,
                    loanStatementRequestDto.getTerm(), loanStatementRequestDto.getAmount()));
            loanOfferDto.setTotalAmount(calculateService.calculatePsk(loanStatementRequestDto.getTerm(),
                    loanOfferDto.getMonthlyPayment()));
            loanOfferDto.setTerm(loanStatementRequestDto.getTerm());
            loanOfferDto.setRate(rate);
            setRateWithStatus(rate, loanOfferDto);
            loanOffersDto.add(loanOfferDto);
        }
        logger.info("The created of offers is successful");
        return loanOffersDto;
    }

    private void setRateWithStatus(BigDecimal rate, LoanOfferDto loanOfferDto) {
        if (Objects.equals(rate, RATE_35)) {
            loanOfferDto.setIsInsuranceEnabled(false);
            loanOfferDto.setIsSalaryClient(false);
        } else if (Objects.equals(rate, BASE_RATE_25)) {
            loanOfferDto.setIsInsuranceEnabled(true);
            loanOfferDto.setIsSalaryClient(false);
        } else if (Objects.equals(rate, RATE_23)) {
            loanOfferDto.setIsInsuranceEnabled(false);
            loanOfferDto.setIsSalaryClient(true);
        } else if (Objects.equals(rate, RATE_20)) {
            loanOfferDto.setIsInsuranceEnabled(true);
            loanOfferDto.setIsSalaryClient(true);
        }
        logger.info("The creation of credit rates was successful");
    }
}
