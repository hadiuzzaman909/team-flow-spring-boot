package com.example.team_flow.dtos.request;


import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private String status;
}