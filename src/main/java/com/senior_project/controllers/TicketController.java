package com.senior_project.controllers;

import com.senior_project.dto.TicketDTO;
import com.senior_project.dto.TicketUpdateDTO;
import com.senior_project.accounts.Role;
import com.senior_project.models.Ticket;
import com.senior_project.models.TicketStatus;
import com.senior_project.services.TicketService;
import com.senior_project.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Ticket Service", description = "Management tickets")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final UserService userService;

    private UUID getUserIdFromPrincipal(Principal principal) {
        String email = principal.getName();

        UUID userId = userService.getUserIdByEmail(email);
        if (userId == null) {
            throw new RuntimeException("User not found for email: " + email);
        }

        return userId;
    }

    private Role getUserRoleFromPrincipal(Principal principal) {
        String email = principal.getName();

        Role role = userService.getRoleByEmail(email);
        if (role == null) {
            throw new RuntimeException("User not found for email: " + email);
        }

        return role;
    }

    // Create ticket, Sancho pass role
    @PostMapping("/createTicket/{role}")
    @Operation(summary = "Create a new ticket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketDTO ticket, @PathVariable Role role, Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ticketService.createTicket(ticket, userId, role));
    }

    // Get tickets by user
    @GetMapping
    @Operation(summary = "Get all tickets created by the user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getTicketsCreatedByUser(Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ticketService.getTicketsCreatedBy(userId));
    }

    // Get tickets by user and given status (status will be passed)
    @GetMapping("/{status}")
    @Operation(summary = "Get tickets created by the user by status.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getTicketsCreatedByUserByStatus(
            @PathVariable TicketStatus status,
            @RequestParam int page,
            @RequestParam int size,
            Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ticketService.getTicketsByStatus(userId, status, pageable));
    }


    // Staff get assigned tickets to them (In Progress)
    @GetMapping("/to-do")
    @Operation(summary = "Get assigned tickets for faculty staff (only in-progress).")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getTicketsAssignedToUser(Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ticketService.getTicketsAssignedTo(userId, TicketStatus.IN_PROGRESS));
    }

    // Staff user assignes ticket to himself (PENDING)
    @PostMapping("/{ticketId}")
    @Operation(summary = "Assign ticket to himself.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ticket> assignedTicketToUser(Principal principal, @PathVariable UUID ticketId) {
        UUID userId = getUserIdFromPrincipal(principal);
        Role userRole = getUserRoleFromPrincipal(principal);
        return ResponseEntity.ok(ticketService.selfAssignTicket(ticketId, userId, userRole));
    }

    // TODO filter by role
    // Get Pending tickets by role
    @GetMapping("/pendingTickets")
    @Operation(summary = "Get pending tickets assigned to the authenticated user's role.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getTicketsByRoleAndStatus(Principal principal) {
        Role userRole = getUserRoleFromPrincipal(principal);
        return ResponseEntity.ok(ticketService.getTicketsByRoleAndStatus(userRole, TicketStatus.PENDING));
    }

    // Update ticket, only user or assigned staff user
    @PutMapping("/{ticketId}")
    @Operation(summary = "Update an existing ticket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ticket> updateTicket(@PathVariable UUID ticketId,
                                               @Valid @RequestBody TicketUpdateDTO ticketUpdateDTO,
                                               Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, ticketUpdateDTO, userId));
    }

    // Delete ticket, only owner can delete
    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Delete an existing ticket (only if created by the user).")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID ticketId, Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        ticketService.deleteTicket(ticketId, userId);
        return ResponseEntity.noContent().build();
    }
}
