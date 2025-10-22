import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { Recipe } from '../../models/recipe.model';
import { Ingredient } from '../../models/ingredient.model';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
  recipeName: string = '';
  recipeInstructions: string = '';
  recipeIngredients: string = '';
  selectedRecipeId: number | null = null;
  recipes: Recipe[] = [];
  isAdmin: boolean = false;

  prepTimeMinutes: number | null = null;
  cookTimeMinutes: number | null = null;
  difficulty: string = '';
  servings: number | null = null;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.http.get<{ role: string }>('https://localhost:8443/api/users/me', {
      withCredentials: true
    }).subscribe({
      next: (user) => {
        if (user && user.role === 'ROLE_ADMIN') {
          this.isAdmin = true;
          this.loadRecipes();
        } else {
          alert('Access denied');
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        console.error('Authentication error for admin:', err);
        alert('Authentication error or not logged in.');
        this.router.navigate(['/']);
      }
    });
  }

  loadRecipes(): void {
    this.http.get<Recipe[]>('https://localhost:8443/api/recipes', {
      withCredentials: true
    }).subscribe({
      next: (data) => this.recipes = data,
      error: (err) => {
        console.error('Failed to load recipes for admin:', err);
        alert('Failed to load recipes');
      }
    });
  }

  selectRecipe(recipe: Recipe): void {
    this.selectedRecipeId = recipe.id;
    this.recipeName = recipe.name;
    this.recipeInstructions = recipe.instructions;
    this.recipeIngredients = recipe.ingredients.map((ing: Ingredient) => ing.name).join(', ');

    this.prepTimeMinutes = recipe.prepTimeMinutes;
    this.cookTimeMinutes = recipe.cookTimeMinutes;
    this.difficulty = recipe.difficulty;
    this.servings = recipe.servings;
  }

  saveRecipe(): void {
    const ingredientsArray: Ingredient[] = this.recipeIngredients.split(',').map(name => ({ name: name.trim() }));

    const recipeData: Partial<Recipe> = {
      name: this.recipeName,
      instructions: this.recipeInstructions,
      ingredients: ingredientsArray,
      prepTimeMinutes: this.prepTimeMinutes || 0,
      cookTimeMinutes: this.cookTimeMinutes || 0,
      difficulty: this.difficulty,
      servings: this.servings || 0
    };

    if (this.selectedRecipeId) {
      this.http.put(`https://localhost:8443/api/recipes/${this.selectedRecipeId}`, recipeData, {
        withCredentials: true
      }).subscribe({
        next: () => {
          alert('Recipe updated!');
          this.clearForm();
          this.loadRecipes();
        },
        error: (err) => {
          console.error('Failed to update recipe:', err);
          alert('Failed to update recipe');
        }
      });
    } else {
      this.http.post('https://localhost:8443/api/recipes', recipeData, {
        withCredentials: true
      }).subscribe({
        next: () => {
          alert('Recipe added!');
          this.clearForm();
          this.loadRecipes();
        },
        error: (err) => {
          console.error('Failed to add recipe:', err);
          alert('Failed to add recipe');
        }
      });
    }
  }

  deleteRecipe(): void {
    if (!this.selectedRecipeId) {
      alert('Please select a recipe to delete.');
      return;
    }

    this.http.delete(`https://localhost:8443/api/recipes/${this.selectedRecipeId}`, {
      withCredentials: true
    }).subscribe({
      next: () => {
        alert('Recipe deleted.');
        this.clearForm();
        this.loadRecipes();
      },
      error: (err) => {
        console.error('Failed to delete recipe:', err);
        alert('Failed to delete recipe');
      }
    });
  }

  clearForm(): void {
    this.recipeName = '';
    this.recipeInstructions = '';
    this.recipeIngredients = '';
    this.selectedRecipeId = null;

    this.prepTimeMinutes = null;
    this.cookTimeMinutes = null;
    this.difficulty = '';
    this.servings = null;
  }
}