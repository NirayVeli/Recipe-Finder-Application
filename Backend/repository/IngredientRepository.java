package com.recipefinder.repository;

import com.recipefinder.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByNameIgnoreCase(String name);

    List<Ingredient> findByNameContainingIgnoreCase(String name);
}