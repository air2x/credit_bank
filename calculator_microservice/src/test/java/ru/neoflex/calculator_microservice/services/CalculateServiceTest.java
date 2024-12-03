package ru.neoflex.calculator_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.neoflex.calculator_microservice.dto.CreditDto;
import ru.neoflex.calculator_microservice.dto.EmploymentDto;
import ru.neoflex.calculator_microservice.dto.ScoringDataDto;
import ru.neoflex.calculator_microservice.util.exceptions.NullMonthlyPaymentException;
import ru.neoflex.calculator_microservice.util.exceptions.NullTermException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static ru.neoflex.calculator_microservice.enums.EmploymentStatus.EMPLOYED;
import static ru.neoflex.calculator_microservice.enums.Gender.MALE;
import static ru.neoflex.calculator_microservice.enums.MaritalStatus.MARRIED;
import static ru.neoflex.calculator_microservice.enums.PositionAtWork.JUNIOR_MANAGER;
import static ru.neoflex.calculator_microservice.services.CalculateService.calculatePsk;

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
    }
}