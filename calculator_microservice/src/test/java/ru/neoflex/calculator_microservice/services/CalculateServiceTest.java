package ru.neoflex.calculator_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.neoflex.calculator_microservice.dto.CreditDto;
import ru.neoflex.calculator_microservice.dto.EmploymentDto;
import ru.neoflex.calculator_microservice.dto.PaymentScheduleElementDto;
import ru.neoflex.calculator_microservice.dto.ScoringDataDto;
import ru.neoflex.calculator_microservice.util.exceptions.NullAmountException;
import ru.neoflex.calculator_microservice.util.exceptions.NullMonthlyPaymentException;
import ru.neoflex.calculator_microservice.util.exceptions.NullRateException;
import ru.neoflex.calculator_microservice.util.exceptions.NullTermException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static ru.neoflex.calculator_microservice.enums.EmploymentStatus.EMPLOYED;
import static ru.neoflex.calculator_microservice.enums.Gender.MALE;
import static ru.neoflex.calculator_microservice.enums.MaritalStatus.MARRIED;
import static ru.neoflex.calculator_microservice.enums.PositionAtWork.JUNIOR_MANAGER;
import static ru.neoflex.calculator_microservice.services.CalculateService.*;
import static ru.neoflex.calculator_microservice.services.LoanOffersService.BASE_RATE_25;
import static ru.neoflex.calculator_microservice.services.LoanOffersService.MONTHS;

class CalculateServiceTest {

    @InjectMocks
    private CalculateService calculateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCreditDtoNotNullTest() {
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        EmploymentDto employmentDto = new EmploymentDto();
        scoringDataDto.setAmount(BigDecimal.valueOf(20000));
        scoringDataDto.setBirthday(LocalDate.ofEpochDay(2000 - 10 - 15));
        scoringDataDto.setTerm(12);
        scoringDataDto.setFirstName("Name");
        scoringDataDto.setLastName("Last name");
        scoringDataDto.setMiddleName("Middle name");
        scoringDataDto.setGender(MALE);
        scoringDataDto.setMaritalStatus(MARRIED);
        scoringDataDto.setIsInsuranceEnabled(true);
        scoringDataDto.setIsSalaryClient(true);
        scoringDataDto.setPassportSeries("1234");
        scoringDataDto.setPassportNumber("987654");

        employmentDto.setSalary(BigDecimal.valueOf(20000));
        employmentDto.setEmploymentStatus(EMPLOYED);
        employmentDto.setPosition(JUNIOR_MANAGER);
        employmentDto.setWorkExperienceTotal(60);
        employmentDto.setWorkExperienceCurrent(12);

        scoringDataDto.setEmployment(employmentDto);

        CreditDto creditDto = calculateService.getCreditDto(scoringDataDto);

        Assertions.assertNotNull(creditDto);
        Assertions.assertNotNull(creditDto.getRate());
        Assertions.assertNotNull(creditDto.getMonthlyPayment());
        Assertions.assertNotNull(creditDto.getTerm());
        Assertions.assertNotNull(creditDto.getPsk());
        Assertions.assertNotNull(creditDto.getAmount());
        Assertions.assertNotNull(creditDto.getPaymentSchedule());
        Assertions.assertNotNull(creditDto.getIsInsuranceEnabled());
        Assertions.assertNotNull(creditDto.getIsSalaryClient());
    }

    @Test
    void calculatePaymentScheduleTest() {
        BigDecimal rate = BigDecimal.valueOf(25);
        int term = 12;
        BigDecimal amount = BigDecimal.valueOf(50000);

        CreditDto creditDto = new CreditDto();
        creditDto.setRate(BASE_RATE_25);
        creditDto.setTerm(term);
        creditDto.setAmount(BigDecimal.valueOf(50000));
        creditDto.setMonthlyPayment(calculateMonthlyPaymentExpected(rate, term, amount));

        List<PaymentScheduleElementDto> paymentScheduleElementDtos = calculatePaymentSchedule(creditDto);

        Assertions.assertNotNull(paymentScheduleElementDtos);
        Assertions.assertEquals(term, paymentScheduleElementDtos.size());

        for (PaymentScheduleElementDto paymentScheduleElementDto : paymentScheduleElementDtos) {
            Assertions.assertNotNull(paymentScheduleElementDto.getDebtPayment());
            Assertions.assertNotNull(paymentScheduleElementDto.getInterestPayment());
            Assertions.assertNotNull(paymentScheduleElementDto.getTotalPayment());
            Assertions.assertNotNull(paymentScheduleElementDto.getDate());
            Assertions.assertNotNull(paymentScheduleElementDto.getNumber());
            Assertions.assertNotNull(paymentScheduleElementDto.getRemainingDebt());
        }
    }

    @Test
    void calculatePskTest() {
        int term = 12;
        BigDecimal monthlyPayment = BigDecimal.valueOf(5000);
        BigDecimal psk = calculatePsk(term, monthlyPayment);
        Assertions.assertEquals(BigDecimal.valueOf(60000), psk);

        Exception ex = Assertions.assertThrows(NullMonthlyPaymentException.class, () -> calculatePsk(term, null));
        Assertions.assertEquals("The monthly payment should not be null", ex.getMessage());

        ex = Assertions.assertThrows(NullTermException.class, () -> calculatePsk(0, monthlyPayment));
        Assertions.assertEquals("The term should not be null", ex.getMessage());
    }

    @Test
    void calculateMonthlyPaymentTest() {
        BigDecimal rate = BigDecimal.valueOf(25);
        int term = 12;
        BigDecimal amount = BigDecimal.valueOf(50000);

        BigDecimal monthlyPaymentExpected = calculateMonthlyPaymentExpected(rate, term, amount);

        BigDecimal monthlyPayment = calculateMonthlyPayment(rate, term, amount);

        Assertions.assertEquals(monthlyPaymentExpected, monthlyPayment);

        Exception ex = Assertions.assertThrows(NullRateException.class, () -> calculateMonthlyPayment(null, term, amount));
        Assertions.assertEquals("Rate is not be null", ex.getMessage());

        ex = Assertions.assertThrows(NullTermException.class, () -> calculateMonthlyPayment(rate, 0, amount));
        Assertions.assertEquals("Term is not be null", ex.getMessage());

        ex = Assertions.assertThrows(NullAmountException.class, () -> calculateMonthlyPayment(rate, term, null));
        Assertions.assertEquals("Amount is not be null", ex.getMessage());
    }

    private BigDecimal calculateMonthlyPaymentExpected(BigDecimal rate, int term, BigDecimal amount) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(MONTHS), RoundingMode.HALF_DOWN)
                .setScale(3, RoundingMode.HALF_UP);
        monthlyRate = (monthlyRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)
                .setScale(3, RoundingMode.HALF_UP)).setScale(3, RoundingMode.HALF_UP);
        BigDecimal partAnnuityRatio = (monthlyRate.add(BigDecimal.valueOf(1))).pow(term);
        BigDecimal monthlyPayment = amount.multiply((monthlyRate.multiply(partAnnuityRatio)).
                divide(partAnnuityRatio.subtract(BigDecimal.valueOf(1)), RoundingMode.HALF_DOWN));
        monthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);
        return monthlyPayment;
    }
}