package com.example.nexus.exception;

public class CampaignAlreadyExistsException extends RuntimeException{
    public CampaignAlreadyExistsException(String msg) {
        super(msg);
    }
}