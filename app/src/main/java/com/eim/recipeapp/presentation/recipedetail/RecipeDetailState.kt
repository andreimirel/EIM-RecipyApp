package com.eim.recipeapp.presentation.recipedetail

import com.eim.recipeapp.domain.model.MealDetail

data class RecipeDetailState(
    val isLoading: Boolean = false,
    val mealDetail: MealDetail? = null,
    val error: String = ""
)
