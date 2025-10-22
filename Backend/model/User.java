package com.recipefinder.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(unique = true)
private String username;

private String password;

private String role;
}

// since i added lombok, should rewrite most of the code with lombok to reduce code usage
// and make it more readable