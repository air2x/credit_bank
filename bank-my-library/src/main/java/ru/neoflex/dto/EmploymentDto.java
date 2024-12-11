package ru.neoflex.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.enums.EmploymentStatus;
import ru.neoflex.enums.EmploymentPosition;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmploymentDto {

    private EmploymentStatus employmentStatus;

    private String employerINN;

    private BigDecimal salary;

    private EmploymentPosition position;

    private Integer workExperienceTotal;

    private Integer workExperienceCurrent;

}
