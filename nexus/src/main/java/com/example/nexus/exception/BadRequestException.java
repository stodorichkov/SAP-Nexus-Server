package com.example.nexus.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg) {
        super(msg);
    }
}
