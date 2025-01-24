package ru.neoflex.calculator_microservice.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.neoflex.calculator_microservice.dto.*;
import ru.neoflex.calculator_microservice.services.CalculateService;
import ru.neoflex.calculator_microservice.services.LoanOffersService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static ru.neoflex.calculator_microservice.enums.EmploymentStatus.EMPLOYED;
import static ru.neoflex.calculator_microservice.enums.Gender.MALE;
import static ru.neoflex.calculator_microservice.enums.MaritalStatus.MARRIED;
import static ru.neoflex.calculator_microservice.enums.PositionAtWork.JUNIOR_MANAGER;

class CalculatorControllerTest {

    @Mock
    private CalculateService calculateService;
    @Mock
    private LoanOffersService loanOffersService;

    @InjectMocks
    private CalculatorController calculatorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void offers() {
        LoanStatementRequestDto loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setAmount(BigDecimal.valueOf(20000));
        loanStatementRequestDto.setTerm(24);
        loanStatementRequestDto.setEmail("test@mail.com");
        List<LoanOfferDto> loanOffersDto = loanOffersService.getLoanOffersDto(loanStatementRequestDto);

        when(loanOffersService.getLoanOffersDto(loanStatementRequestDto)).thenReturn(loanOffersDto);
        verify(loanOffersService).getLoanOffersDto(loanStatementRequestDto);
    }

    @Test
    void testOffers() {
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

        when(calculateService.getCreditDto(scoringDataDto)).thenReturn(creditDto);
        verify(calculateService).getCreditDto(scoringDataDto);
    }
}