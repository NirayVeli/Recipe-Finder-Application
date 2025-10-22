import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Observable, debounceTime, distinctUntilChanged, switchMap, of, Subscription } from 'rxjs';

import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

import { Recipe } from '../../models/recipe.model';
import { Ingredient } from '../../models/ingredient.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent implements OnInit, OnDestroy {
  ingredients: string[] = [];
  recipes: Recipe[] = [];
  selectedRecipe: Recipe | null = null;

  ingredientFormControl = new FormControl('');
  filteredIngredients!: Observable<string[]>;

  isLoggedIn: boolean = false;
  private authStatusSubscription!: Subscription;

  constructor(private http: HttpClient, private authService: AuthService) {}

  ngOnInit(): void {
    this.authStatusSubscription = this.authService.isLoggedIn$.subscribe(
      status => {
        this.isLoggedIn = status;
      }
    );

    this.filteredIngredients = this.ingredientFormControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => {
        if (value && value.length > 1) {
          return this._filterIngredients(value);
        } else {
          return of([]);
        }
      })
    );
  }

  ngOnDestroy(): void {
    if (this.authStatusSubscription) {
      this.authStatusSubscription.unsubscribe();
    }
  }

  private _filterIngredients(value: string): Observable<string[]> {
    const filterValue = value.toLowerCase();
    return this.http.get<Ingredient[]>(`https://localhost:8443/api/ingredients/search?name=${filterValue}`).pipe(
      switchMap(ingredients => {
        if (ingredients && ingredients.length > 0) {
          return of(ingredients.map(ing => ing.name));
        } else {
          return of([]);
        }
      })
    );
  }

  addIngredient(): void {
    const selectedIngredient = this.ingredientFormControl.value;
    if (selectedIngredient && selectedIngredient.trim() !== '' && !this.ingredients.includes(selectedIngredient.trim())) {
      this.ingredients.push(selectedIngredient.trim());
      this.ingredientFormControl.setValue('');
    }
  }

  removeIngredient(ingredient: string): void {
    this.ingredients = this.ingredients.filter(i => i !== ingredient);
  }

  searchRecipes(): void {
    if (this.ingredients.length > 0) {
      this.http.post<Recipe[]>('https://localhost:8443/api/recipes/search', this.ingredients, {
        withCredentials: true
      }).subscribe({
        next: (data) => {
          this.recipes = data;
          this.selectedRecipe = null;
          if (this.recipes.length === 0) {
            alert('No recipes found for the selected ingredients.');
          }
        },
        error: (err) => {
          console.error('Error searching recipes:', err);
          alert('Error loading recipes.');
        }
      });
    } else {
      this.recipes = [];
      this.selectedRecipe = null;
      alert('Please add at least one ingredient to search.');
    }
  }

  showDetails(recipe: Recipe): void {
    this.selectedRecipe = recipe;
  }

  getIngredientsDisplay(recipe: Recipe): string {
    if (recipe && recipe.ingredients) {
      return recipe.ingredients.map((ing: Ingredient) => ing.name).join(', ');
    }
    return '';
  }
}