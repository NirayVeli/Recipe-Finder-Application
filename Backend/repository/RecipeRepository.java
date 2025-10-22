package com.recipefinder.repository;

import com.recipefinder.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.ingredients i WHERE LOWER(i.name) IN :ingredientNames")
    List<Recipe> findByIngredientNamesIgnoreCase(@Param("ingredientNames") List<String> ingredientNames);
}
