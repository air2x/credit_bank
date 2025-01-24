package ru.neoflex.calculator_microservice.util.exceptions;

public class NullMonthlyPaymentException extends RuntimeException {

    public NullMonthlyPaymentException(String message) {
        super(message);
    }
}
