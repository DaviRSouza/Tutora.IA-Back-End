package br.com.tutoraia.exceptions;

import lombok.Getter;

@Getter
public class InvalidParamsException extends RuntimeException{

    private final String name = "InvalidParamsException";
    public InvalidParamsException(String message) {
        super(message);
    }
}
