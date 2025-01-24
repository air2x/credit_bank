package ru.neoflex.calculator_microservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanStatementRequestDto {

    @NotNull
    @DecimalMin(value = "20000", message = "Сумма должна быть меньше или равно 20000")
    private BigDecimal amount;

    @Min(value = 6, message = "Срок кредита должен быть больше или равен 6 месяцам")
    private Integer term;

    @NotNull(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно быть не меньше 2 и не больше 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Имя должно содержать только латинские буквы")
    private String firstName;

    @NotNull(message = "Фамилия не должна быть пустым")
    @Size(min = 2, max = 30, message = "Фамилия должна быть не меньше 2 и не больше 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия должна содержать только латинские буквы")
    private String lastName;

    @Size(min = 2, max = 30, message = "Фамилия должна быть не меньше 2 и не больше 30 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия должна содержать только латинские буквы")
    private String middleName;

    @NotNull
    @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$", message = "Некорректный email адрес")
    private String email;

    private LocalDate birthday;

    @NotNull
    @Pattern(regexp = "^\\d{4}$", message = "Номер паспорта состоит из 4 цифр")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "^\\d{6}$", message = "Серия паспорта состоит из 6 цифр")
    private String passportNumber;
}
