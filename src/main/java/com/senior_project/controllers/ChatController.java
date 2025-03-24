package com.senior_project.controllers;

import com.senior_project.models.Chat;
import com.senior_project.models.Message;
import com.senior_project.services.ChatService;
import com.senior_project.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    @GetMapping("/{ticketId}")
    public ResponseEntity<Chat> getChatByTicketId(@PathVariable UUID ticketId) {
        Chat chat = chatService.getChatByTicketId(ticketId);
        return ResponseEntity.ok(chat);
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable UUID chatId,
            @RequestParam UUID senderId,
            @RequestBody String content) {
        Message message = messageService.sendMessage(chatId, senderId, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable UUID chatId) {
        List<Message> messages = messageService.getMessagesByChatId(chatId);
        return ResponseEntity.ok(messages);
    }
}
