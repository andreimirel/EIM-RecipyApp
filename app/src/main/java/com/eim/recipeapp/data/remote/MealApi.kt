package com.eim.recipeapp.data.remote

import com.eim.recipeapp.data.remote.dto.MealDetailDto
import com.eim.recipeapp.data.remote.dto.MealsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") searchQuery: String): MealsDto

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") mealId: String): MealDetailDto

    @GET("random.php")
    suspend fun getRandomMeal(): MealDetailDto
}
