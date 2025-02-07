package com.senior_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.senior_project.models.TicketPriority;
import com.senior_project.models.TicketStatus;

@Data
public class TicketUpdateDTO {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Status is required")
    private TicketStatus status; // PENDING, IN_PROGRESS, COMPLETED, CANCELED

    @NotNull(message = "Priority is required")
    private TicketPriority priority; // LOW, MEDIUM, HIGH
}
