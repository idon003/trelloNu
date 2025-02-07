package com.senior_project.services;

import com.senior_project.accounts.Role;
import com.senior_project.accounts.User;
import com.senior_project.dto.TicketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.senior_project.dto.TicketUpdateDTO;
import com.senior_project.models.Ticket;
import com.senior_project.models.TicketStatus;
import com.senior_project.repository.TicketRepository;

import java.awt.print.Pageable;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    // Create a ticket
    public Ticket createTicket(TicketDTO ticketDTO, UUID userId, Role role) {
        Ticket ticket = new Ticket();
        ticket.setCreatedBy(userId);
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setTitle(ticketDTO.getTitle());
        ticket.setPriority(ticketDTO.getPriority());
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setAssignedRole(role);
        ticket.setAssignedTo(null);
        return ticketRepository.save(ticket);
    }

    // Assign a ticket to the authenticated user
    public Ticket selfAssignTicket(UUID ticketId, UUID userId, Role userRole) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found!"));

        if (ticket.getAssignedTo() != null) {
            throw new IllegalStateException("This ticket is already assigned to another user.");
        }

        if (!ticket.getAssignedRole().equals(userRole)) {
            throw new SecurityException("You do not have permission to self-assign this ticket.");
        }

        ticket.setAssignedTo(userId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    // Get tickets created by a user
    public List<Ticket> getTicketsCreatedBy(UUID userId) {
        return ticketRepository.findByCreatedBy(userId);
    }

    // Get tickets assigned to a specific role with a status
    public List<Ticket> getTicketsByRoleAndStatus(Role role, TicketStatus status) {
        return ticketRepository.findByAssignedRoleAndStatus(role, status);
    }

    // Get tickets assigned to a specific user with a status
    public List<Ticket> getTicketsAssignedTo(UUID userId, TicketStatus status) {
        return ticketRepository.findByAssignedToAndStatus(userId, status);
    }

    // Update a ticket (only creator or assigned user can update)
    public Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketUpdateDTO, UUID userId) {
        Ticket existingTicket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found!"));

        if (!existingTicket.getCreatedBy().equals(userId) &&
                (existingTicket.getAssignedTo() == null || !existingTicket.getAssignedTo().equals(userId))) {
            throw new SecurityException("You do not have permission to update this ticket.");
        }

        existingTicket.setTitle(ticketUpdateDTO.getTitle());
        existingTicket.setDescription(ticketUpdateDTO.getDescription());
        existingTicket.setStatus(ticketUpdateDTO.getStatus());

        return ticketRepository.save(existingTicket);
    }

    // Delete a ticket (only the creator can delete)
    public void deleteTicket(UUID ticketId, UUID userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found!"));

        if (!ticket.getCreatedBy().equals(userId)) {
            throw new SecurityException("You do not have permission to delete this ticket.");
        }

        ticketRepository.delete(ticket);
    }

    public List<Ticket> getTicketsByStatus(UUID userId, TicketStatus status, PageRequest page) {
        return ticketRepository.findByCreatedByAndStatus(userId, status, page);
    }


}
