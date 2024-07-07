package com.starhealth.login.exceptions;

public class InvalidEmailException extends RuntimeException{
    private String message;

    public InvalidEmailException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
