package ru.neoflex.deal_microservice.model;

import ru.neoflex.deal_microservice.enums.EmploymentPosition;
import ru.neoflex.deal_microservice.enums.EmploymentStatus;

import java.math.BigDecimal;

public class Employment {
    private EmploymentStatus status;
    private String inn;
    private BigDecimal salary;
    private EmploymentPosition position;
    private int workExperienceTotal;
    private int workExperienceCurrent;
}
