package ru.neoflex.calculator_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.calculator_microservice.enums.EmploymentStatus;
import ru.neoflex.calculator_microservice.enums.PositionAtWork;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmploymentDto {

    private EmploymentStatus employmentStatus;

    private String employerINN;

    private BigDecimal salary;

    private PositionAtWork position;

    private Integer workExperienceTotal;

    private Integer workExperienceCurrent;

}
