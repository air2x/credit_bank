package ru.neoflex.calculator_microservice.util.exceptions;

public class NullTermException extends RuntimeException {
    public NullTermException(String message) {
        super(message);
    }
}
