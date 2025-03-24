package com.senior_project.services;

import com.senior_project.models.Chat;
import com.senior_project.models.Ticket;
import com.senior_project.repository.ChatRepository;
import com.senior_project.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public void createChatForTicket(Ticket ticket) {
        if (chatRepository.findByTicketId(ticket.getId()).isPresent()) {
            throw new IllegalStateException("Chat already exists for this ticket.");
        }

        Chat chat = new Chat();
        chat.setTicket(ticket);
        chat.setOwnerId(ticket.getCreatedBy());
        chat.setAssignedUserId(ticket.getAssignedTo());

        chatRepository.save(chat);
    }

    public Chat getChatByTicketId(UUID ticketId) {
        return chatRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for this ticket."));
    }
}
