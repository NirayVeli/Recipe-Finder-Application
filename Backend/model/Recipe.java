package com.recipefinder.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipes")
public class Recipe {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private String name;

@Column(nullable = false, length = 1000)
private String instructions;

private Integer prepTimeMinutes; // prep/podgotovka - minutes
private Integer cookTimeMinutes; // cooking - minutes
private String difficulty;       // difficulty/trudnost (Easy, Medium, Hard)
private Integer servings;        // servings/porcii

@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
@JoinTable(
        name = "recipe_ingredient",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
)
private Set<Ingredient> ingredients = new HashSet<>();

public Recipe() {
}

public Recipe(String name, String instructions, Set<Ingredient> ingredients, Integer prepTimeMinutes, Integer cookTimeMinutes, String difficulty, Integer servings) {
    this.name = name;
    this.instructions = instructions;
    this.ingredients = ingredients;
    this.prepTimeMinutes = prepTimeMinutes;
    this.cookTimeMinutes = cookTimeMinutes;
    this.difficulty = difficulty;
    this.servings = servings;
}

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getInstructions() {
    return instructions;
}

public void setInstructions(String instructions) {
    this.instructions = instructions;
}

public Set<Ingredient> getIngredients() {
    return ingredients;
}

public void setIngredients(Set<Ingredient> ingredients) {
    this.ingredients = ingredients;
}

public Integer getPrepTimeMinutes() {
    return prepTimeMinutes;
}

public void setPrepTimeMinutes(Integer prepTimeMinutes) {
    this.prepTimeMinutes = prepTimeMinutes;
}

public Integer getCookTimeMinutes() {
    return cookTimeMinutes;
}

public void setCookTimeMinutes(Integer cookTimeMinutes) {
    this.cookTimeMinutes = cookTimeMinutes;
}

public String getDifficulty() {
    return difficulty;
}

public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
}

public Integer getServings() {
    return servings;
}

public void setServings(Integer servings) {
    this.servings = servings;
}

@Override
public String toString() {
    return "Recipe{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", instructions='" + instructions + '\'' +
            ", prepTimeMinutes=" + prepTimeMinutes +
            ", cookTimeMinutes=" + cookTimeMinutes +
            ", difficulty='" + difficulty + '\'' +
            ", servings=" + servings +
            ", ingredients=" + ingredients +
            '}';
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Recipe recipe = (Recipe) o;
    return id != null && id.equals(recipe.id); // Id for comparing
}

@Override
public int hashCode() {
    return getClass().hashCode(); // Objects.hash(id)???
}
}