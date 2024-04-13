package com.example.nexus.exception;

public class PaymentException extends RuntimeException {
    public PaymentException(String msg) {
        super(msg);
    }
}