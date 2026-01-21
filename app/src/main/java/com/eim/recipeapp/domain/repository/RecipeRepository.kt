package com.eim.recipeapp.domain.repository

import com.eim.recipeapp.domain.model.Meal
import com.eim.recipeapp.domain.model.MealDetail
import com.eim.recipeapp.domain.model.NutritionItem
import com.eim.recipeapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchMeals(query: String): List<Meal>
    suspend fun getMealDetail(mealId: String): MealDetail

    // New function for fetching nutrition data
    suspend fun getNutritionInfo(query: String): Resource<List<NutritionItem>>

    suspend fun addFavoriteMeal(mealDetail: MealDetail)
    suspend fun removeFavoriteMeal(mealId: String)
    fun getFavoriteMeals(): Flow<List<MealDetail>>
    suspend fun isMealFavorite(mealId: String): Boolean
    suspend fun clearLocalFavorites()
}
