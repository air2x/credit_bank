package ru.neoflex.deal_microservice.model;

import ru.neoflex.deal_microservice.enums.CreditStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class Credit {
    private UUID creditId;
    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    //private PaymentSchedule
    private Boolean insuranceEnabled;
    private Boolean salaryClient;
    private CreditStatus creditStatus;
}
