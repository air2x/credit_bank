package ru.neoflex.model;

import ru.neoflex.dto.PaymentScheduleElementDto;
import ru.neoflex.enums.CreditStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class Credit {
    private UUID creditId;
    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private PaymentScheduleElementDto paymentSchedule;
    private Boolean insuranceEnabled;
    private Boolean salaryClient;
    private CreditStatus creditStatus;
}
