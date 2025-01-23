package ru.neoflex.calculator_microservice.util.exceptions;

public class LoanIsNotApprovedException extends RuntimeException {

    public LoanIsNotApprovedException(String message) {
        super(message);
    }

}
