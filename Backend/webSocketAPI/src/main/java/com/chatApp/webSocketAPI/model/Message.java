package com.chatApp.webSocketAPI.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ID;

    private String content;

    private LocalDateTime timeStamp;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chat chat;

    public Message(){}

    public Message(Integer ID, String content, LocalDateTime timeStamp, User user, Chat chat) {
        this.ID = ID;
        this.content = content;
        this.timeStamp = timeStamp;
        this.user = user;
        this.chat = chat;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }


}
