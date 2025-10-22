package com.recipefinder.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ingredient")
public class Ingredient {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false, unique = true)
private String name;
// empty for jpa???
public Ingredient() {}

public Ingredient(String name) {
    this.name = name;
}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getName() { return name; }
public void setName(String name) { this.name = name; }

@Override
public String toString() {
    return "Ingredient{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
}
}