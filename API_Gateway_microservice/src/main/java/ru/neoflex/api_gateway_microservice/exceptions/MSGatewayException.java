package ru.neoflex.api_gateway_microservice.exceptions;

public class MSGatewayException extends RuntimeException {

    public MSGatewayException(String message) {
        super(message);
    }
}
