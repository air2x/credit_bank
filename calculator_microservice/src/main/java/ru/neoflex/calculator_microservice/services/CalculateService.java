package ru.neoflex.calculator_microservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.util.exceptions.*;
import ru.neoflex.dto.CreditDto;
import ru.neoflex.dto.EmploymentDto;
import ru.neoflex.dto.PaymentScheduleElementDto;
import ru.neoflex.dto.ScoringDataDto;
import ru.neoflex.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static ru.neoflex.calculator_microservice.services.LoanOffersService.BASE_RATE_25;
import static ru.neoflex.calculator_microservice.services.LoanOffersService.MONTHS;
import static ru.neoflex.enums.EmploymentPosition.MID_MANAGER;
import static ru.neoflex.enums.EmploymentPosition.TOP_MANAGER;
import static ru.neoflex.enums.EmploymentStatus.SELF_EMPLOYED;
import static ru.neoflex.enums.EmploymentStatus.UNEMPLOYED;
import static ru.neoflex.enums.Gender.*;
import static ru.neoflex.enums.MaritalStatus.DIVORCED;
import static ru.neoflex.enums.MaritalStatus.MARRIED;

@Slf4j
@Service
public class CalculateService {

    public static final int MIN_AGE = 20;
    public static final int MAX_AGE = 65;
    public static final int MIN_WORK_EXPERIENCE_TOTAL = 18;
    public static final int MIN_WORK_EXPERIENCE_CURRENT = 3;
    public static final int MIN_MALE_AGE = 30;
    public static final int MAX_MALE_AGE = 55;
    public static final int MIN_FEMALE_AGE = 32;
    public static final int MAX_FEMALE_AGE = 60;
    public static final int MONTHS_24 = 24;
    public static final BigDecimal RATE_1 = BigDecimal.valueOf(1.0);
    public static final BigDecimal RATE_2 = BigDecimal.valueOf(2.0);
    public static final BigDecimal RATE_3 = BigDecimal.valueOf(3.0);
    public static final BigDecimal RATE_7 = BigDecimal.valueOf(7.0);
    public static final BigDecimal RATE_0 = BigDecimal.valueOf(0.0);

    public CreditDto getCreditDto(ScoringDataDto scoringDataDto) {
        if (isLoanOk(scoringDataDto)) {
            return calculateCredit(scoringDataDto);
        } else {
            throw new LoanIsNotApprovedException("The loan is not approved");
        }
    }

    public static List<PaymentScheduleElementDto> calculatePaymentSchedule(CreditDto creditDto) {
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

    public static BigDecimal calculatePsk(int term, BigDecimal monthlyPayment) {
        if (monthlyPayment != null && term != 0) {
            return BigDecimal.valueOf(term).multiply(monthlyPayment);
        } else if (monthlyPayment == null){
            throw new NullMonthlyPaymentException("The monthly payment should not be null");
        } else {
            throw new NullTermException("The term should not be null");
        }
    }

    public static BigDecimal calculateMonthlyPayment(BigDecimal rate, int term, BigDecimal amount) {
        if (rate != null && term != 0 && amount != null) {
            BigDecimal monthlyPayment;
            BigDecimal monthlyRate = calculateMonthlyRate(rate);
            BigDecimal partAnnuityRatio = (monthlyRate.add(BigDecimal.valueOf(1))).pow(term);
            monthlyPayment = amount.multiply((monthlyRate.multiply(partAnnuityRatio)).
                    divide(partAnnuityRatio.subtract(BigDecimal.valueOf(1)), RoundingMode.HALF_DOWN));
            log.info("The monthly payment has been calculated successfully");
            return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
        } else if (rate == null) {
            log.info("The monthly payment has not been calculated successfully because rate is null");
            throw new NullRateException("Rate is not be null");
        } else if (term == 0) {
            log.info("The monthly payment has not been calculated successfully because term is null");
            throw new NullTermException("Term is not be null");
        } else {
            log.info("The monthly payment has not been calculated successfully because amount is null");
            throw new NullAmountException("Amount is not be null");
        }
    }

    private CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        CreditDto creditDto = new CreditDto();
        creditDto.setAmount(scoringDataDto.getAmount());
        creditDto.setTerm(scoringDataDto.getTerm());
        creditDto.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
        creditDto.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());
        creditDto.setRate(calculateRate(scoringDataDto));
        creditDto.setMonthlyPayment(calculateMonthlyPayment(creditDto.getRate(), creditDto.getTerm(), creditDto.getAmount()));
        creditDto.setPsk(calculatePsk(scoringDataDto.getTerm(), creditDto.getMonthlyPayment()));
        creditDto.setPaymentSchedule(calculatePaymentSchedule(creditDto));
        log.info(scoringDataDto.getAccountNumber() + " The creation of the loan offer was successful");
        return creditDto;
    }

    private static BigDecimal calculateMonthlyRate(BigDecimal rate) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(MONTHS), RoundingMode.HALF_DOWN)
                .setScale(3, RoundingMode.HALF_UP);
        monthlyRate = (monthlyRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)
                .setScale(3, RoundingMode.HALF_UP)).setScale(3, RoundingMode.HALF_UP);
        return monthlyRate;
    }

    private BigDecimal calculateRate(ScoringDataDto scoringDataDto) {
        BigDecimal tempRate = BASE_RATE_25;
        EmploymentDto employmentDto = scoringDataDto.getEmployment();
        if (employmentDto.getEmploymentStatus() == SELF_EMPLOYED) {
            tempRate = tempRate.add(RATE_2);
        } else if (employmentDto.getEmploymentStatus().equals(EmploymentStatus.BUSINESS_OWNER)) {
            tempRate = tempRate.add(RATE_1);
        }
        if (employmentDto.getPosition() == MID_MANAGER) {
            tempRate = tempRate.subtract(RATE_2);
        } else if (employmentDto.getPosition() == TOP_MANAGER) {
            tempRate = tempRate.subtract(RATE_3);
        }
        if (scoringDataDto.getMaritalStatus() == MARRIED) {
            tempRate = tempRate.subtract(RATE_3);
        } else if (scoringDataDto.getMaritalStatus() == DIVORCED) {
            tempRate = tempRate.add(RATE_1);
        }
        tempRate = tempRate.subtract(getRateWithGenderAndAge(scoringDataDto));
        log.info(scoringDataDto.getAccountNumber() + " The rate has been calculated successfully");
        return tempRate;
    }

    private BigDecimal getRateWithGenderAndAge(ScoringDataDto scoringDataDto) {
        int age;
        if (scoringDataDto.getBirthday() != null) {
            age = calculateAge(scoringDataDto);
        } else {
            throw new NullBirthDayException("Birthday is not be null");
        }
        if (age != 0 && scoringDataDto.getGender() != null && isAgeOk(scoringDataDto)) {
            if ((scoringDataDto.getGender() == FEMALE && age >= MIN_FEMALE_AGE && age <= MAX_FEMALE_AGE) ||
                    (scoringDataDto.getGender() == MALE && age >= MIN_MALE_AGE && age <= MAX_MALE_AGE)) {
                return RATE_3;
            } else if (scoringDataDto.getGender() == NON_BINARY) {
                return RATE_7;
            }
        } else {
            throw new GenderException("Gender error");
        }
        return RATE_1;
    }

    private boolean isLoanOk(ScoringDataDto scoringDataDto) {
        if (scoringDataDto.getEmployment() != null) {
            EmploymentDto employmentDto = scoringDataDto.getEmployment();
            return isWorkExperienceOk(scoringDataDto) &&
                    employmentDto.getEmploymentStatus() != UNEMPLOYED &&
                    isSalaryRelativeRequestedAmountOk(scoringDataDto) &&
                    isAgeOk(scoringDataDto);
        } else {
            log.info(scoringDataDto.getAccountNumber() + " Employment is null");
            throw new NullEmploymentException("Employment is not be null");
        }
    }

    private boolean isSalaryRelativeRequestedAmountOk(ScoringDataDto scoringDataDto) {
        BigDecimal salaryFor24Months = scoringDataDto.getEmployment().getSalary().multiply(BigDecimal.valueOf(MONTHS_24));
        if (salaryFor24Months.compareTo(scoringDataDto.getAmount()) >= 0) {
            return true;
        } else {
            throw new LoanIsNotApprovedException("Salary relative to the requested amount is not ok");
        }
    }

    private boolean isWorkExperienceOk(ScoringDataDto scoringDataDto) {
        if (scoringDataDto.getEmployment().getWorkExperienceTotal() >= MIN_WORK_EXPERIENCE_TOTAL &&
                scoringDataDto.getEmployment().getWorkExperienceCurrent() >= MIN_WORK_EXPERIENCE_CURRENT) {
            return true;
        } else {
            throw new LoanIsNotApprovedException("The loan was not approved due to lack of work experience");
        }
    }

    private boolean isAgeOk(ScoringDataDto scoringDataDto) {
        if (calculateAge(scoringDataDto) <= MAX_AGE && calculateAge(scoringDataDto) >= MIN_AGE) {
            return true;
        } else {
            throw new LoanIsNotApprovedException("The loan was not approved due to age");
        }
    }

    private int calculateAge(ScoringDataDto scoringDataDto) {
        if (scoringDataDto.getBirthday() != null) {
            return Period.between(scoringDataDto.getBirthday(), LocalDate.now()).getYears();
        } else {
            throw new NullBirthDayException(scoringDataDto.getAccountNumber() + "Birthday is not be null");
        }
    }
}
