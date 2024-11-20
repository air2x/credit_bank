package ru.neoflex.calculator_microservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.calculator_microservice.enums.Gender;
import ru.neoflex.calculator_microservice.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScoringDataDto {

    @NotEmpty
    @DecimalMin(value = "20000", message = "Сумма должна быть меньше или равно 20000")
    private BigDecimal amount;

    @Min(value = 6, message = "Срок кредита должен быть больше или равен 6")
    private Integer term;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно быть не меньше 2 и не больше 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Имя должно содержать только латинские буквы")
    private String firstName;

    @NotEmpty(message = "Фамилия не должна быть пустым")
    @Size(min = 2, max = 30, message = "Фамилия должна быть не меньше 2 и не больше 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия должна содержать только латинские буквы")
    private String lastName;

    @Size(min = 2, max = 30, message = "Фамилия должна быть не меньше 2 и не больше 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия должна содержать только латинские буквы")
    private String middleName;

    @NotEmpty
    private Gender gender;

    @NotEmpty
    private LocalDate birthday;

    @NotEmpty
    @Pattern(regexp = "^\\d{4}$", message = "Номер паспорта состоит из 4 цифр")
    private String passportSeries;

    @NotEmpty
    @Pattern(regexp = "^\\d{6}$", message = "Серия паспорта состоит из 6 цифр")
    private String passportNumber;

    @NotEmpty
    private LocalDate passportIssueDate;

    @NotEmpty
    private String passportIssueBranch;

    @NotEmpty
    private MaritalStatus maritalStatus;

    @NotEmpty
    private Integer dependentAmount;

    @NotEmpty
    private EmploymentDto employment;

    @NotEmpty
    private String accountNumber;

    @NotEmpty
    private Boolean isInsuranceEnabled;

    @NotEmpty
    private Boolean isSalaryClient;
}
