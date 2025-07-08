package com.partner.exception.exceptions;

public class InvalidToken extends RuntimeException{
    public InvalidToken(String message){
        super(message);
    }
}
