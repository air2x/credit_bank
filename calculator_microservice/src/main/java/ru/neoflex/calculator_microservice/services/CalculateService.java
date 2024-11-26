package ru.neoflex.calculator_microservice.services;

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
import java.util.List;

import static ru.neoflex.calculator_microservice.services.LoanOffersService.BASE_RATE_25;
import static ru.neoflex.calculator_microservice.services.LoanOffersService.MONTHS;

/*
1. По API приходит ScoringDataDto.
2. Происходит скоринг данных, высчитывание итоговой ставки(rate), полной стоимости кредита(psk),
размер ежемесячного платежа(monthlyPayment), график ежемесячных платежей (List<PaymentScheduleElementDto>).
3. Логику расчета параметров кредита можно найти в интернете, полученный результат сверять с имеющимися в
    интернете калькуляторами графиков платежей и ПСК.
Ответ на API - CreditDto, насыщенный всеми рассчитанными параметрами.

Правила скоринга (можно придумать новые правила или изменить существующие):
1. Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 2; Владелец бизнеса → ставка увеличивается на 1
2. Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на 3
3. Сумма займа больше, чем 24 зарплат → отказ
4. Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1
5. Возраст менее 20 или более 65 лет → отказ
6. Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3; Мужчина, возраст от 30 до 55 лет → ставка
    уменьшается на 3; Не бинарный → ставка увеличивается на 7
7. Стаж работы: Общий стаж менее 18 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
 */
@Service
public class CalculateService {

    public static final int MIN_AGE = 20;
    public static final int MAX_AGE = 65;
    public static final int MIN_WORK_EXPERIENCE_TOTAL = 18;
    public static final int MIN_WORK_EXPERIENCE_CURRENT = 3;

    public CreditDto getCreditDto(ScoringDataDto scoringDataDto) {
        if (isLoanRefusal(scoringDataDto)) {
            return calculateCredit(scoringDataDto);
        }
        return null;
    }

    private CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        CreditDto creditDto = new CreditDto();
        BigDecimal psk = calculatePsk(scoringDataDto);
        creditDto.setAmount(scoringDataDto.getAmount());
        creditDto.setTerm(scoringDataDto.getTerm());
        creditDto.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
        creditDto.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());
        creditDto.setRate(calculateRate(scoringDataDto));
        creditDto.setMonthlyPayment(calculateMonthlyPayment(creditDto.getRate(), creditDto.getTerm(), creditDto.getAmount()));
        creditDto.setPsk(psk);
        creditDto.setPaymentSchedule(calculatePaymentSchedule(psk));
        return creditDto;
    }

    private List<PaymentScheduleElementDto> calculatePaymentSchedule(BigDecimal psk) {
        return null;
    }

    private BigDecimal calculatePsk(ScoringDataDto scoringDataDto) {
        return null;
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal rate, int term, BigDecimal amount) {
        BigDecimal monthlyPayment;
        BigDecimal monthlyRate = (rate.divide(BigDecimal.valueOf(MONTHS))).divide(BigDecimal.valueOf(100));
        BigDecimal temp = (monthlyRate.add(BigDecimal.valueOf(1))).pow(term);
        monthlyPayment = amount.multiply((monthlyRate.multiply(temp)).
                divide(temp.subtract(BigDecimal.valueOf(1)), RoundingMode.HALF_UP));
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
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
