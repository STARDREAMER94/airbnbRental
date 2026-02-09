package services;

import models.Message;
import utils.FileHandler;
import utils.SecurityUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MessageService {
    private List<Message> messages;
    private static final String MESSAGES_FILE = "messages.txt";

    public MessageService() {
        loadMessages();
    }

    private void loadMessages() {
        messages = FileHandler.loadData(MESSAGES_FILE, Message::fromString);
    }

    public void saveMessages() {
        FileHandler.saveData(MESSAGES_FILE, messages);
    }

    public boolean sendMessage(String senderId, String receiverId, String subject, String content) {
        Message message = new Message(
            SecurityUtils.generateId(),
            senderId,
            receiverId,
            subject,
            content
        );
        
        messages.add(message);
        return FileHandler.appendData(MESSAGES_FILE, message);
    }

    public List<Message> getMessagesForUser(String userId) {
        return messages.stream()
                .filter(message -> message.getReceiverId().equals(userId) || 
                                 message.getSenderId().equals(userId))
                .sorted((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()))
                .collect(Collectors.toList());
    }

    public List<Message> getConversation(String user1Id, String user2Id) {
        return messages.stream()
                .filter(message -> 
                    (message.getSenderId().equals(user1Id) && message.getReceiverId().equals(user2Id)) ||
                    (message.getSenderId().equals(user2Id) && message.getReceiverId().equals(user1Id))
                )
                .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
                .collect(Collectors.toList());
    }

    public void markMessageAsRead(String messageId) {
        messages.stream()
                .filter(message -> message.getMessageId().equals(messageId))
                .findFirst()
                .ifPresent(Message::markAsRead);
        saveMessages();
    }

    public long getUnreadMessageCount(String userId) {
        return messages.stream()
                .filter(message -> message.getReceiverId().equals(userId) && !message.isRead())
                .count();
    }
}