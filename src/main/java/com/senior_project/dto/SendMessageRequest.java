package com.senior_project.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageRequest {
    private UUID senderId;
    private String content;
}
