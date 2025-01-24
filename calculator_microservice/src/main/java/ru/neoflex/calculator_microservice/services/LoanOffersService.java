package ru.neoflex.calculator_microservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;
import ru.neoflex.calculator_microservice.util.exceptions.NullEmailException;
import ru.neoflex.calculator_microservice.util.exceptions.NullRateException;

import java.math.BigDecimal;
import java.util.*;

import static ru.neoflex.calculator_microservice.services.CalculateService.calculateMonthlyPayment;
import static ru.neoflex.calculator_microservice.services.CalculateService.calculatePsk;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class LoanOffersService {

    public static final BigDecimal RATE_20 = BigDecimal.valueOf(20.0);
    public static final BigDecimal RATE_23 = BigDecimal.valueOf(23.0);
    public static final BigDecimal BASE_RATE_25 = BigDecimal.valueOf(25.0);
    public static final BigDecimal RATE_35 = BigDecimal.valueOf(35.0);
    public static final List<BigDecimal> RATES = Arrays.asList(RATE_35, BASE_RATE_25, RATE_23, RATE_20);
    public static final int MONTHS = 12;


    public List<LoanOfferDto> getLoanOffersDto(LoanStatementRequestDto loanStatementRequestDto) {
        List<LoanOfferDto> loanOffersDto = new ArrayList<>();
        if (loanStatementRequestDto.getEmail() != null) {
            for (BigDecimal rate : RATES) {
                LoanOfferDto loanOfferDto = new LoanOfferDto();
                loanOfferDto.setStatementId(UUID.randomUUID());
                loanOfferDto.setRequestAmount(loanStatementRequestDto.getAmount());
                loanOfferDto.setMonthlyPayment(calculateMonthlyPayment(rate,
                        loanStatementRequestDto.getTerm(), loanStatementRequestDto.getAmount()));
                loanOfferDto.setTotalAmount(calculatePsk(loanStatementRequestDto.getTerm(),
                        loanOfferDto.getMonthlyPayment()));
                loanOfferDto.setTerm(loanStatementRequestDto.getTerm());
                loanOfferDto.setRate(rate);
                setClientStatus(rate, loanOfferDto);
                loanOffersDto.add(loanOfferDto);
            }
            log.info(loanStatementRequestDto.getEmail() + " The created of offers is successful");
        } else {
            throw new NullEmailException("Email is not be null");
        }
        return loanOffersDto;
    }

    private void setClientStatus(BigDecimal rate, LoanOfferDto loanOfferDto) {
        if (rate != null) {
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
            log.info("The creation of credit rates was successful");
        } else {
            throw new NullRateException("Rate is not be null");
        }
    }
}
