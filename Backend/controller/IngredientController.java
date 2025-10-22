package com.recipefinder.controller;

import com.recipefinder.model.Ingredient;
import com.recipefinder.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class IngredientController {

@Autowired
private IngredientRepository ingredientRepository;

@GetMapping("/search")
public ResponseEntity<List<Ingredient>> searchIngredientsByName(@RequestParam String name) {
    if (name == null || name.trim().isEmpty()) {
        // return blank list
        return ResponseEntity.ok(List.of());
    }
    // findByNameContainingIgnoreCase add in repository
    List<Ingredient> ingredients = ingredientRepository.findByNameContainingIgnoreCase(name);
    return ResponseEntity.ok(ingredients);
}
}