package ru.neoflex.calculator_microservice.services;

import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/*
1. По API приходит LoanStatementRequestDto.
2. На основании LoanStatementRequestDto происходит прескоринг, создаётся 4 кредитных предложения LoanOfferDto на основании
всех возможных комбинаций булевских полей isInsuranceEnabled и isSalaryClient (false-false, false-true, true-false, true-true).
Логику формирования кредитных предложений можно придумать самому.
К примеру: в зависимости от страховых услуг увеличивается/уменьшается процентная ставка и сумма кредита, базовая ставка
хардкодится в коде через property файл. Например цена страховки 100к (или прогрессивная, в зависимости от запрошенной
суммы кредита), ее стоимость добавляется в тело кредита, но она уменьшает ставку на 3. Цена зарплатного клиента 0, уменьшает ставку на 1.
3. Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему" (чем меньше итоговая ставка, тем лучше).
 */
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
