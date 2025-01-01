package com.example.team_flow.exceptions;

public class TaskBadRequestException extends RuntimeException {

    public TaskBadRequestException(String message) {
        super(message);
    }
}
