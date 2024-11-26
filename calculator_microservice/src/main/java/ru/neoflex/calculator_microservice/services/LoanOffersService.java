package ru.neoflex.calculator_microservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
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
        List<BigDecimal> rates = Arrays.asList(RATE_35, BASE_RATE_25, RATE_23, RATE_20);
        List<LoanOfferDto> loanOffersDto = new ArrayList<>();
        for (BigDecimal rate : rates) {
            LoanOfferDto loanOfferDto = new LoanOfferDto();
            loanOfferDto.setStatementId(UUID.randomUUID());
            loanOfferDto.setRequestAmount(loanStatementRequestDto.getAmount());
            loanOfferDto.setMonthlyPayment(calculateService.calculateMonthlyPayment(rate, loanStatementRequestDto.getTerm(), loanStatementRequestDto.getAmount()));
            loanOfferDto.setTotalAmount(totalAmountCalc(loanStatementRequestDto, loanOfferDto));
            loanOfferDto.setTerm(loanStatementRequestDto.getTerm());
            loanOfferDto.setRate(rate);
            setRateWithStatus(rate, loanOfferDto);
            loanOffersDto.add(loanOfferDto);
        }
        return loanOffersDto;
    }

    public BigDecimal totalAmountCalc(LoanStatementRequestDto loanStatementRequestDto, LoanOfferDto loanOfferDto) {
        return BigDecimal.valueOf(loanStatementRequestDto.getTerm()).multiply(loanOfferDto.getMonthlyPayment());
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
    }
}
