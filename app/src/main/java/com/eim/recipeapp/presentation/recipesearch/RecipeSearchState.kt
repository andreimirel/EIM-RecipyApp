package com.eim.recipeapp.presentation.recipesearch

import com.eim.recipeapp.domain.model.Meal

data class RecipeSearchState(
    val isLoading: Boolean = false,
    val meals: List<Meal> = emptyList(),
    val error: String = ""
)
