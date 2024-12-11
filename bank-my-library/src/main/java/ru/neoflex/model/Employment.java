package ru.neoflex.model;

import ru.neoflex.enums.EmploymentPosition;
import ru.neoflex.enums.EmploymentStatus;

import java.math.BigDecimal;

public class Employment {
    private EmploymentStatus status;
    private String inn;
    private BigDecimal salary;
    private EmploymentPosition position;
    private int workExperienceTotal;
    private int workExperienceCurrent;
}
