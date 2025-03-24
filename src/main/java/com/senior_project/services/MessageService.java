package com.senior_project.services;

import com.senior_project.models.Chat;
import com.senior_project.models.Message;
import com.senior_project.repository.ChatRepository;
import com.senior_project.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public Message sendMessage(UUID chatId, UUID senderId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found!"));
        System.out.println(chat.getOwnerId());
        System.out.println(chat.getAssignedUserId());
        System.out.println(senderId);
        if (!chat.getOwnerId().equals(senderId) && !chat.getAssignedUserId().equals(senderId)) {
            throw new SecurityException("You are not part of this chat.");
        }

        Message message = Message.builder()
                .chat(chat)
                .senderId(senderId)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    public List<Message> getMessagesByChatId(UUID chatId) {
        return messageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }
}
