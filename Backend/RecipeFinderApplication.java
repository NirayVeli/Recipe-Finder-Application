package com.recipefinder;

import com.recipefinder.model.Ingredient;
import com.recipefinder.model.Recipe;
import com.recipefinder.model.User;
import com.recipefinder.repository.IngredientRepository;
import com.recipefinder.repository.RecipeRepository;
import com.recipefinder.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootApplication
public class RecipeFinderApplication {

public static void main(String[] args) {
	SpringApplication.run(RecipeFinderApplication.class, args);
}

// commandline runner transaction?
@Bean
@Transactional
public CommandLineRunner initData(
		PasswordEncoder encoder,
		UserRepository userRepository,
		RecipeRepository recipeRepository,
		IngredientRepository ingredientRepository
) {
	return args -> {
		// admin
		if (userRepository.findByUsername("admin").isEmpty()) {
			String hashedPassword = encoder.encode("1234");
			System.out.println("Creating admin user with hashed password: " + hashedPassword);

			User adminUser = new User();
			adminUser.setUsername("admin");
			adminUser.setPassword(hashedPassword);
			adminUser.setRole("ROLE_ADMIN");

			userRepository.save(adminUser);
			System.out.println("Admin user 'admin' created successfully.");
		} else {
			System.out.println("Admin user 'admin' already exists.");
		}

		if (recipeRepository.count() == 0) {
			System.out.println("Initializing sample recipes...");

			List<String> ingredientNames = Arrays.asList(
					"Tomatoes", "Pasta", "Chicken", "Broccoli", "Garlic", "Olive Oil", "Salt", "Black Pepper", "Potatoes", "Milk"
			);

			java.util.Map<String, Ingredient> managedIngredientsMap = new HashMap<>();

			// find by name

			for (String name : ingredientNames) {
				Optional<Ingredient> existingIngredientOpt = ingredientRepository.findByNameIgnoreCase(name);
				Ingredient currentIngredient;

				if (existingIngredientOpt.isPresent()) {
					currentIngredient = existingIngredientOpt.get();
				} else {
					currentIngredient = ingredientRepository.save(new Ingredient(name));
				}
				managedIngredientsMap.put(name.toLowerCase(), currentIngredient);
			}

			// manual injection of recipes for testing

			// Pasta Pomodoro
			Recipe pastaPomodoro = new Recipe();
			pastaPomodoro.setName("Pasta Pomodoro");
			pastaPomodoro.setInstructions("Boil pasta. Sauté chopped tomatoes and garlic in olive oil. Combine with pasta and fresh basil.");
			pastaPomodoro.setPrepTimeMinutes(15);
			pastaPomodoro.setCookTimeMinutes(20);
			pastaPomodoro.setDifficulty("Easy");
			pastaPomodoro.setServings(4);
			pastaPomodoro.setIngredients(new HashSet<>(Arrays.asList(
					managedIngredientsMap.get("tomatoes"),
					managedIngredientsMap.get("pasta"),
					managedIngredientsMap.get("garlic"),
					managedIngredientsMap.get("olive oil"),
					managedIngredientsMap.get("salt")
			)));
			recipeRepository.save(pastaPomodoro);

			// Grilled Chicken with Broccoli
			Recipe grilledChicken = new Recipe();
			grilledChicken.setName("Grilled Chicken with Broccoli");
			grilledChicken.setInstructions("Season chicken breast and grill until cooked through. Steam broccoli until tender-crisp. Serve together.");
			grilledChicken.setPrepTimeMinutes(20);
			grilledChicken.setCookTimeMinutes(25);
			grilledChicken.setDifficulty("Medium");
			grilledChicken.setServings(2);
			grilledChicken.setIngredients(new HashSet<>(Arrays.asList(
					managedIngredientsMap.get("chicken"),
					managedIngredientsMap.get("broccoli"),
					managedIngredientsMap.get("garlic"),
					managedIngredientsMap.get("olive oil"),
					managedIngredientsMap.get("salt"),
					managedIngredientsMap.get("black pepper")
			)));
			recipeRepository.save(grilledChicken);

			// Mashed Potatoes
			Recipe mashedPotatoes = new Recipe();
			mashedPotatoes.setName("Creamy Mashed Potatoes");
			mashedPotatoes.setInstructions("Boil potatoes until tender. Mash with butter, hot milk, salt, and pepper until smooth and creamy.");
			mashedPotatoes.setPrepTimeMinutes(10);
			mashedPotatoes.setCookTimeMinutes(20);
			mashedPotatoes.setDifficulty("Easy");
			mashedPotatoes.setServings(4);
			mashedPotatoes.setIngredients(new HashSet<>(Arrays.asList(
					managedIngredientsMap.get("potatoes"),
					managedIngredientsMap.get("milk"),
					managedIngredientsMap.get("salt"),
					managedIngredientsMap.get("black pepper")
			)));
			recipeRepository.save(mashedPotatoes);

			System.out.println("DEBUG: Sample recipes initialized successfully.");
		} else {
			System.out.println("DEBUG: Recipes already exist. Skipping sample recipe initialization.");
		}
	};
}
}