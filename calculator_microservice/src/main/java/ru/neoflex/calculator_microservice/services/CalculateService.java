package ru.neoflex.calculator_microservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.dto.CreditDto;
import ru.neoflex.calculator_microservice.dto.EmploymentDto;
import ru.neoflex.calculator_microservice.dto.PaymentScheduleElementDto;
import ru.neoflex.calculator_microservice.dto.ScoringDataDto;
import ru.neoflex.calculator_microservice.enums.EmploymentStatus;
import ru.neoflex.calculator_microservice.enums.Gender;
import ru.neoflex.calculator_microservice.enums.MaritalStatus;
import ru.neoflex.calculator_microservice.enums.PositionAtWork;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static ru.neoflex.calculator_microservice.services.LoanOffersService.BASE_RATE_25;
import static ru.neoflex.calculator_microservice.services.LoanOffersService.MONTHS;

@Slf4j
@Service
public class CalculateService {

    public static final int MIN_AGE = 20;
    public static final int MAX_AGE = 65;
    public static final int MIN_WORK_EXPERIENCE_TOTAL = 18;
    public static final int MIN_WORK_EXPERIENCE_CURRENT = 3;

    public CreditDto getCreditDto(ScoringDataDto scoringDataDto) {
        if (!isLoanRefusal(scoringDataDto)) {
            return calculateCredit(scoringDataDto);
        }
        return null;
    }

    private CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        CreditDto creditDto = new CreditDto();
        BigDecimal psk;
        creditDto.setAmount(scoringDataDto.getAmount());
        creditDto.setTerm(scoringDataDto.getTerm());
        creditDto.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
        creditDto.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());
        creditDto.setRate(calculateRate(scoringDataDto));
        creditDto.setMonthlyPayment(calculateMonthlyPayment(creditDto.getRate(), creditDto.getTerm(), creditDto.getAmount()));
        psk = calculatePsk(scoringDataDto.getTerm(), creditDto.getMonthlyPayment());
        creditDto.setPsk(psk);
        creditDto.setPaymentSchedule(calculatePaymentSchedule(creditDto));
        log.info("The creation of the loan offer was successful");
        return creditDto;
    }

    public List<PaymentScheduleElementDto> calculatePaymentSchedule(CreditDto creditDto) {
        List<PaymentScheduleElementDto> paymentScheduleElementDtos = new ArrayList<>();
        BigDecimal balanceOfTheDebt = creditDto.getAmount();
        BigDecimal monthlyRate = calculateMonthlyRate(creditDto.getRate());
        LocalDate dateNow = LocalDate.now();
        for (int i = 1; i <= creditDto.getTerm(); i++) {
            PaymentScheduleElementDto paymentScheduleElementDto = new PaymentScheduleElementDto();
            BigDecimal percent = balanceOfTheDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal repaymentMainDebt = creditDto.getMonthlyPayment().subtract(percent).setScale(2, RoundingMode.HALF_UP);
            balanceOfTheDebt = balanceOfTheDebt.subtract(repaymentMainDebt).setScale(2, RoundingMode.HALF_UP);
            paymentScheduleElementDto.setNumber(i);
            paymentScheduleElementDto.setTotalPayment(percent.add(repaymentMainDebt));
            paymentScheduleElementDto.setDebtPayment(creditDto.getMonthlyPayment().subtract(percent));
            paymentScheduleElementDto.setInterestPayment(percent);
            paymentScheduleElementDto.setRemainingDebt(balanceOfTheDebt);
            paymentScheduleElementDto.setDate(dateNow.plusMonths(i));
            paymentScheduleElementDtos.add(paymentScheduleElementDto);
        }
        log.info("The payment schedule was created successfully");
        return paymentScheduleElementDtos;
    }

    public BigDecimal calculatePsk(int term, BigDecimal monthlyPayment) {
        return BigDecimal.valueOf(term).multiply(monthlyPayment);
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal rate, int term, BigDecimal amount) {
        BigDecimal monthlyPayment;
        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        BigDecimal temp = (monthlyRate.add(BigDecimal.valueOf(1))).pow(term);
        monthlyPayment = amount.multiply((monthlyRate.multiply(temp)).
                divide(temp.subtract(BigDecimal.valueOf(1)), RoundingMode.HALF_DOWN));
        log.info("The monthly payment has been calculated successfully");
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyRate(BigDecimal rate) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(MONTHS), RoundingMode.HALF_DOWN)
                .setScale(3, RoundingMode.HALF_UP);
        monthlyRate = monthlyRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)
                .setScale(3, RoundingMode.HALF_UP);
        return monthlyRate.setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRate(ScoringDataDto scoringDataDto) {
        BigDecimal tempRate = BASE_RATE_25;
        EmploymentDto employmentDto = scoringDataDto.getEmployment();
        if (employmentDto.getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED)) {
            tempRate = tempRate.add(BigDecimal.valueOf(2));
        } else if (employmentDto.getEmploymentStatus().equals(EmploymentStatus.BUSINESS_OWNER)) {
            tempRate = tempRate.add(BigDecimal.valueOf(1));
        }
        if (employmentDto.getPosition().equals(PositionAtWork.MIDDLE_MANAGER)) {
            tempRate = tempRate.subtract(BigDecimal.valueOf(2));
        } else if (employmentDto.getPosition().equals(PositionAtWork.TOP_MANAGER)) {
            tempRate = tempRate.subtract(BigDecimal.valueOf(3));
        }
        if (scoringDataDto.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            tempRate = tempRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringDataDto.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            tempRate = tempRate.add(BigDecimal.valueOf(1));
        }
        if (scoringDataDto.getGender().equals(Gender.FEMALE) &&
                calculateAge(scoringDataDto) >= 32 &&
                calculateAge(scoringDataDto) <= 60) {
            tempRate = tempRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringDataDto.getGender().equals(Gender.MALE) &&
                calculateAge(scoringDataDto) >= 30 &&
                calculateAge(scoringDataDto) <= 55) {
            tempRate = tempRate.subtract(BigDecimal.valueOf(3));
        }
        log.info("The rate has been calculated successfully");
        return tempRate;
    }

    private boolean isLoanRefusal(ScoringDataDto scoringDataDto) {
        EmploymentDto employmentDto = scoringDataDto.getEmployment();
        return (employmentDto.getWorkExperienceTotal() >= MIN_WORK_EXPERIENCE_TOTAL) &&
                employmentDto.getWorkExperienceCurrent() >= MIN_WORK_EXPERIENCE_CURRENT &&
                !employmentDto.getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED) &&
                scoringDataDto.getAmount().compareTo(employmentDto.getSalary().multiply(BigDecimal.valueOf(24))) >= 0 &&
                calculateAge(scoringDataDto) <= MAX_AGE &&
                calculateAge(scoringDataDto) >= MIN_AGE;
    }

    private int calculateAge(ScoringDataDto scoringDataDto) {
        return Period.between(scoringDataDto.getBirthday(), LocalDate.now()).getYears();
    }
}
