package com.hein.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import javax.annotation.Nonnull;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String avatar;
    private String email;
    private String name;
    private String password;
    private String phone;
    private int role;
    private String username;

    public User() {}

    public User(String avatar, String email, String name, String password, String phone, int role, String username) {
        this.avatar = avatar;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
