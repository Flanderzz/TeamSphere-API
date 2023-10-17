package com.chatApp.webSocketAPI.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ID;
    private String name;
    private String email;
    private String profile_pic;
    private String password;

    public User (){}

    public User(Integer ID, String name, String email, String profile_pic, String password) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.profile_pic = profile_pic;
        this.password = password;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(ID, user.ID) && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(profile_pic, user.profile_pic) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, name, email, profile_pic, password);
    }
}
