package io.bierzan.fivedayforecast.forecast.exception;

public class PostalCodeValidationException extends RuntimeException{
    public PostalCodeValidationException(String message) {
        super(message);
    }
}
