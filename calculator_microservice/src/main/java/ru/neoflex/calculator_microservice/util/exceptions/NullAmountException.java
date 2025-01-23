package ru.neoflex.calculator_microservice.util.exceptions;

public class NullAmountException extends RuntimeException {
    public NullAmountException(String message) {
        super(message);
    }
}
