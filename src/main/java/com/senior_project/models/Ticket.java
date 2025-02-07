package com.senior_project.models;


import com.senior_project.accounts.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tickets",
        indexes = {
                @Index(name = "idx_ticket_status", columnList = "status"),
                @Index(name = "idx_ticket_role", columnList = "assignedRole")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;    // PENDING, IN_PROGRESS, COMPLETED, CANCELED

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;    // LOW, MEDIUM, HIGH

//    private LocalDateTime dueDate;

    @Column(nullable = false)
    private UUID createdBy; // user who created the ticket

    @Column(nullable = true)
    private UUID assignedTo; // can be null at pending state then assigned to a faculty user

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role assignedRole;  // will be taken from url
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
