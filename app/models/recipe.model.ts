import { Ingredient } from './ingredient.model';

export interface Recipe {
  id: number;
  name: string;
  instructions: string;
  ingredients: Ingredient[];
  prepTimeMinutes: number;
  cookTimeMinutes: number;
  difficulty: string;
  servings: number;
}