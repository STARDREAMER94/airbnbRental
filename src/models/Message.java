package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String subject;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    public Message(String messageId, String senderId, String receiverId, 
                  String subject, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.subject = subject;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and setters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public boolean isRead() { return isRead; }

    public void markAsRead() { this.isRead = true; }

    @Override
    public String toString() {
        return String.join(",",
            messageId, senderId, receiverId, subject, content,
            sentAt.toString(), String.valueOf(isRead)
        );
    }

    public static Message fromString(String data) {
        String[] parts = data.split(",");
        Message message = new Message(
            parts[0], parts[1], parts[2], parts[3], parts[4]
        );
        message.sentAt = LocalDateTime.parse(parts[5]);
        message.isRead = Boolean.parseBoolean(parts[6]);
        return message;
    }
}