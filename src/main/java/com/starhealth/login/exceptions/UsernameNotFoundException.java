package com.starhealth.login.exceptions;

public class UsernameNotFoundException extends RuntimeException{

    private String message;

    public UsernameNotFoundException(String message) {
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
