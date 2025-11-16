package com.eim.recipeapp.domain.repository

import com.eim.recipeapp.domain.model.Meal
import com.eim.recipeapp.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchMeals(query: String): List<Meal>
    suspend fun getMealDetail(mealId: String): MealDetail

    suspend fun addFavoriteMeal(mealDetail: MealDetail)
    suspend fun removeFavoriteMeal(mealId: String)
    fun getFavoriteMeals(): Flow<List<MealDetail>>
    suspend fun isMealFavorite(mealId: String): Boolean
    suspend fun clearLocalFavorites()
}
