package ru.neoflex.dto;

import ru.neoflex.enums.Gender;
import ru.neoflex.enums.MaritalStatus;

import java.time.LocalDate;

public class FinishRegistrationRequestDto {

    private Gender gender;
    private MaritalStatus maritalStatus;
    private LocalDate passportIssueDate;
    private String passportIssueBrach;
    private EmploymentDto employment;
    private String accountNumber;

}
