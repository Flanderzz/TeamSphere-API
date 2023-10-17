package com.chatApp.webSocketAPI.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ManyToAny;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ID;

    private String chatName;
    private String chatImage;


    @Column(name = "is_group_chat")
    private boolean isGroupChat;

    @JoinColumn(name = "created_by")
    @ManyToOne
    private User createdBy;

    @ManyToMany
    private Set<User> admins = new HashSet<> ();

    @ManyToMany
    private Set<User> users = new HashSet<> ();

    @OneToMany
    private List<Message> messages;

    public Chat (){}

    public Chat(Integer ID, String chatName, String chatImage, boolean isGroupChat, User createdBy, Set<User> admins, Set<User> users, List<Message> messages) {
        this.ID = ID;
        this.chatName = chatName;
        this.chatImage = chatImage;
        this.isGroupChat = isGroupChat;
        this.createdBy = createdBy;
        this.admins = admins;
        this.users = users;
        this.messages = messages;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatImage() {
        return chatImage;
    }

    public void setChatImage(String chatImage) {
        this.chatImage = chatImage;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
