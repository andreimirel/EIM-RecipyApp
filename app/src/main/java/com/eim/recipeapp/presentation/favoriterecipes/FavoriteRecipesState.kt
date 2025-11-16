package com.eim.recipeapp.presentation.favoriterecipes

import com.eim.recipeapp.domain.model.MealDetail

data class FavoriteRecipesState(
    val isLoading: Boolean = false,
    val favoriteMeals: List<MealDetail> = emptyList(),
    val error: String = ""
)
