package ru.neoflex.calculator_microservice.services;

import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class LoanOffersService {

    public static final Double RATE_20 = 20.0;
    public static final Double RATE_23 = 23.0;
    public static final Double RATE_25 = 25.0;
    public static final Double RATE_35 = 35.0;
    public static final int MONTHS = 12;

    public List<LoanOfferDto> getLoanOffersDto(LoanStatementRequestDto loanStatementRequestDto) {
        List<Double> rates = Arrays.asList(RATE_35, RATE_25, RATE_23, RATE_20);
        List<LoanOfferDto> loanOffersDto = new ArrayList<>();
        for (Double rate : rates) {
            LoanOfferDto loanOfferDto = new LoanOfferDto();
            loanOfferDto.setStatementId(UUID.randomUUID());
            loanOfferDto.setRequestAmount(loanStatementRequestDto.getAmount());
            loanOfferDto.setMonthlyPayment(monthlyPaymentCalc(loanStatementRequestDto, rate));
            loanOfferDto.setTotalAmount(totalAmountCalc(loanStatementRequestDto, loanOfferDto));
            loanOfferDto.setTerm(loanStatementRequestDto.getTerm());
            loanOfferDto.setRate(BigDecimal.valueOf(rate));
            setRateWithStatus(rate, loanOfferDto);
            loanOffersDto.add(loanOfferDto);
        }
        return loanOffersDto;
    }

    public BigDecimal monthlyPaymentCalc(LoanStatementRequestDto loanStatementRequestDto, Double rate) {
        BigDecimal monthlyPayment;
        BigDecimal monthlyRate = BigDecimal.valueOf((rate / MONTHS) / 100);
        BigDecimal temp = (monthlyRate.add(BigDecimal.valueOf(1))).pow(loanStatementRequestDto.getTerm());
        monthlyPayment = loanStatementRequestDto.getAmount().multiply((monthlyRate.multiply(temp)).
                divide(temp.subtract(BigDecimal.valueOf(1)), RoundingMode.HALF_UP));
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal totalAmountCalc(LoanStatementRequestDto loanStatementRequestDto, LoanOfferDto loanOfferDto) {
        return BigDecimal.valueOf(loanStatementRequestDto.getTerm()).multiply(loanOfferDto.getMonthlyPayment());
    }

    private void setRateWithStatus(Double rate, LoanOfferDto loanOfferDto) {
        if (Objects.equals(rate, RATE_35)) {
            loanOfferDto.setIsInsuranceEnabled(false);
            loanOfferDto.setIsSalaryClient(false);
        } else if (Objects.equals(rate, RATE_25)) {
            loanOfferDto.setIsInsuranceEnabled(true);
            loanOfferDto.setIsSalaryClient(false);
        } else if (Objects.equals(rate, RATE_23)) {
            loanOfferDto.setIsInsuranceEnabled(false);
            loanOfferDto.setIsSalaryClient(true);
        } else if (Objects.equals(rate, RATE_20)) {
            loanOfferDto.setIsInsuranceEnabled(true);
            loanOfferDto.setIsSalaryClient(true);
        }
    }
}
