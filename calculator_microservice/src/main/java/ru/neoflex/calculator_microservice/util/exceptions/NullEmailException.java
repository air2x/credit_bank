package ru.neoflex.calculator_microservice.util.exceptions;

public class NullEmailException extends RuntimeException {
    public NullEmailException(String message) {
        super(message);
    }
}
