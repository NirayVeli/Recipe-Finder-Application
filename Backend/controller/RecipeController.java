package com.recipefinder.controller;

import com.recipefinder.model.Ingredient;
import com.recipefinder.model.Recipe;
import com.recipefinder.repository.IngredientRepository;
import com.recipefinder.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/recipes")
// using default localhost stuff - change later versions
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RecipeController {

@Autowired
private RecipeRepository recipeRepository;

@Autowired
private IngredientRepository ingredientRepository;

@GetMapping
public ResponseEntity<List<Recipe>> getAllRecipes() {
    System.out.println("Entering getAllRecipes endpoint.");
    List<Recipe> recipes = recipeRepository.findAll();
    System.out.println("found " + recipes.size() + " recipes from repository.");
    recipes.forEach(r -> System.out.println("  recipe ID: " + r.getId() + ", name: " + r.getName() + ", Instructions: " + r.getInstructions()));
    return ResponseEntity.ok(recipes);
}

@GetMapping("/{id}")
public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
    Optional<Recipe> recipe = recipeRepository.findById(id);
    return recipe.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PostMapping("/search")
public ResponseEntity<List<Recipe>> searchRecipes(@RequestBody List<String> ingredients) {
    if (ingredients == null || ingredients.isEmpty()) {
        return ResponseEntity.badRequest().body(null);
    }
    List<String> lowerCaseIngredients = ingredients.stream()
            .map(String::toLowerCase)
            .toList();

    // method from RecipeRepository - look later
    List<Recipe> recipes = recipeRepository.findByIngredientNamesIgnoreCase(lowerCaseIngredients);
    return ResponseEntity.ok(recipes);
}
// Admin Role, change it to a separate controller later versions after tests

@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
    Set<Ingredient> managedIngredients = new HashSet<>();
    if (recipe.getIngredients() != null) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            Optional<Ingredient> existingIngredient = ingredientRepository.findByNameIgnoreCase(ingredient.getName());
            if (existingIngredient.isPresent()) {
                managedIngredients.add(existingIngredient.get());
            } else {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setName(ingredient.getName());
                managedIngredients.add(ingredientRepository.save(newIngredient));
            }
        }
    }
    recipe.setIngredients(managedIngredients);

    Recipe savedRecipe = recipeRepository.save(recipe);
    return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
}

@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe updatedRecipe) {
    return recipeRepository.findById(id)
            .map(existingRecipe -> {
                existingRecipe.setName(updatedRecipe.getName());
                existingRecipe.setInstructions(updatedRecipe.getInstructions());
                existingRecipe.setPrepTimeMinutes(updatedRecipe.getPrepTimeMinutes());
                existingRecipe.setCookTimeMinutes(updatedRecipe.getCookTimeMinutes());
                existingRecipe.setDifficulty(updatedRecipe.getDifficulty());
                existingRecipe.setServings(updatedRecipe.getServings());

                Set<Ingredient> managedIngredients = new HashSet<>();
                if (updatedRecipe.getIngredients() != null) {
                    for (Ingredient ingredient : updatedRecipe.getIngredients()) {
                        Optional<Ingredient> existingIngredient = ingredientRepository.findByNameIgnoreCase(ingredient.getName());
                        if (existingIngredient.isPresent()) {
                            managedIngredients.add(existingIngredient.get());
                        } else {
                            Ingredient newIngredient = new Ingredient();
                            newIngredient.setName(ingredient.getName());
                            managedIngredients.add(ingredientRepository.save(newIngredient));
                        }
                    }
                }
                existingRecipe.setIngredients(managedIngredients);

                Recipe savedRecipe = recipeRepository.save(existingRecipe);
                return ResponseEntity.ok(savedRecipe);
            })
            .orElse(ResponseEntity.notFound().build());
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
    if (recipeRepository.existsById(id)) {
        recipeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    } else {
        return ResponseEntity.notFound().build();
    }
}

}