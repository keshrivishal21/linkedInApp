package com.vishal.linkedInProject.connectionsService.exception;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String message){
        super(message);
    }
}
