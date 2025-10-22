package com.recipefinder.dto;

import com.recipefinder.model.User;

public class UserDto {
private Long id;
private String username;
private String role;

public UserDto(Long id, String username, String role) {
    this.id = id;
    this.username = username;
    this.role = role;
}

// Constructor form User entity
public UserDto(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.role = user.getRole();
}

public Long getId() { return id; }
public String getUsername() { return username; }
public String getRole() { return role; }
public void setId(Long id) { this.id = id; }
public void setUsername(String username) { this.username = username; }
public void setRole(String role) { this.role = role; }
}