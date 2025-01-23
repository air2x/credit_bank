package ru.neoflex.calculator_microservice.util.exceptions;

public class NullBirthDayException extends RuntimeException {
    public NullBirthDayException(String message) {
        super(message);
    }
}
