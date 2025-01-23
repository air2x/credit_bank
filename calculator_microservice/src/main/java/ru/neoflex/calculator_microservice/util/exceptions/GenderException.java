package ru.neoflex.calculator_microservice.util.exceptions;

public class GenderException extends RuntimeException {
    public GenderException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
