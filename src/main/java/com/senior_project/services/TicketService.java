package com.senior_project.services;

import com.senior_project.accounts.Role;
import com.senior_project.accounts.User;
import com.senior_project.dto.TicketDTO;
import com.senior_project.models.Chat;
import com.senior_project.models.TicketPriority;
import com.senior_project.repository.ChatRepository;
import com.senior_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.senior_project.dto.TicketUpdateDTO;
import com.senior_project.models.Ticket;
import com.senior_project.models.TicketStatus;
import com.senior_project.repository.TicketRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    //    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatService chatService;

    private User find(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }

    public Ticket createTicket(TicketDTO ticketDTO, UUID userId, Role role) {
        Ticket ticket = new Ticket();
        ticket.setCreatedBy(userId);
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setTitle(ticketDTO.getTitle());
        ticket.setPriority(ticketDTO.getPriority());
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setAssignedRole(role);
        ticket.setAssignedTo(null);

        Ticket savedTicket = ticketRepository.save(ticket);

        chatService.createChatForTicket(savedTicket);

        return savedTicket;
    }

    public Ticket selfAssignTicket(UUID ticketId, UUID userId, Role userRole) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found!"));

        if (ticket.getAssignedTo() != null) {
            throw new IllegalStateException("This ticket is already assigned to another user.");
        }

        if (!ticket.getAssignedRole().equals(userRole)) {
            throw new SecurityException("You do not have permission to self-assign this ticket.");
        }

        ticket.setAssignedTo(userId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        // Update assignedUser in the related chat
        Chat chat = chatService.getChatByTicketId(ticket.getId());
        chat.setAssignedUserId(userId);
        chatRepository.save(chat);

        return ticketRepository.save(ticket);
    }


    // Get tickets created by a user
    public List<Ticket> getTicketsCreatedBy(UUID userId) {
        return ticketRepository.findByCreatedBy(userId);
    }

    // Get tickets assigned to a specific role with a status
    public Page<Ticket> getTicketsByRoleAndStatus(Role role, Pageable pageable) {
        return ticketRepository.findByRoleAndStatus(role, TicketStatus.PENDING, pageable);
    }

    // Get tickets assigned to a specific user with a status
    public Page<Ticket> getTicketsAssignedTo(UUID userId, TicketStatus status, Pageable pageable) {
        return ticketRepository.findByAssignedToAndStatus(userId, status, pageable);
    }

    // Update a ticket (only creator or assigned user can update)
    public Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketUpdateDTO, UUID userId) {
        Ticket existingTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found!"));

        boolean isCreator = existingTicket.getCreatedBy().equals(userId);
        boolean isAssignedUser = existingTicket.getAssignedTo() != null && existingTicket.getAssignedTo().equals(userId);

        if (isCreator) {
            // Creator can update title, description, and priority
            existingTicket.setTitle(ticketUpdateDTO.getTitle());
            existingTicket.setDescription(ticketUpdateDTO.getDescription());
            existingTicket.setPriority(ticketUpdateDTO.getPriority());
        }

        if (isAssignedUser) {
            existingTicket.setStatus(ticketUpdateDTO.getStatus());

//            User creator = find(existingTicket.getCreatedBy());
//            emailService.sendEmail(creator.getEmail(), "Status changed to " + ticketUpdateDTO.getStatus(), "Dear " + creator.getFirstName() + " " + creator.getLastName() + ", your ticket '" + existingTicket.getTitle() + "' status has changed to " + ticketUpdateDTO.getStatus() + ".");
        }

        if (!isCreator && !isAssignedUser) {
            throw new SecurityException("You do not have permission to update this ticket.");
        }

        return ticketRepository.save(existingTicket);
    }


    // Delete a ticket (only the creator can delete)
    public void deleteTicket(UUID ticketId, UUID userId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found!"));

        if (!ticket.getCreatedBy().equals(userId)) {
            throw new SecurityException("You do not have permission to delete this ticket.");
        }

        ticketRepository.delete(ticket);
    }

    public Page<Ticket> getTicketsByStatus(UUID userId, TicketStatus status, Pageable page) {
        return ticketRepository.findByCreatedByAndStatus(userId, status, page);
    }

    public Page<Ticket> filterTickets(TicketStatus status, TicketPriority priority, Role role, Pageable page) {
        return ticketRepository.filterTickets(status, priority, role, page);
    }

    public Ticket getTicketById(UUID ticketId) {

        return ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found!"));
    }

    public Page<Ticket> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> getCompletedTicketsByUser(UUID userId, Pageable pageable) {
        return ticketRepository.findByAssignedToAndStatus(userId, TicketStatus.COMPLETED, pageable);
    }
}
